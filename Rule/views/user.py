from views import sessionTime, isCorruptedToken, getDataFromToken, createSessionToken, sessionTimeExpired
from django.views.decorators.csrf import csrf_exempt
from django.core.exceptions import ObjectDoesNotExist
from django.http import JsonResponse, HttpResponse, QueryDict
from django.db.models import Q
from Rule.models import User
import random, time, string

def auth(req):
    if req.method == 'GET':
        try:
            login = req.GET['l']
            password = req.GET['p']
        except KeyError:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            user = User.objects.get(phone_number = login, password = password)
        except ObjectDoesNotExist:
            return JsonResponse({
                'msg': "not_found"
            }, status = 404)
        user.prv_key = random.randint(2**16, 2**32)
        user.sessionExpTime = int(time.time()) + sessionTime
        user.refresh = ''.join(random.choices(string.ascii_lowercase + string.digits, k=128))
        user.save()
        return JsonResponse({
            'response': {
                'token': createSessionToken({
                    'id': user.id,
                    'location_id': -1
                }, user.prv_key),
                'refresh': user.refresh
            }
        }, status = 200)
    return HttpResponse(status = 405)

def register(req):
    if req.method == 'PUT':
        nickName = req.PUT['nick']
        password = req.PUT['password']
        phone = req.PUT['phone']
        email = req.PUT['email']
        name = req.PUT['name']
        query = Q(phone_number__startswith = phone) | Q(email__startswith = email)
        user = User.objects.filter(query)
        
        if len(user) == 0:
            new_user = User(nickName = nickName, name = name, email = email, phone_number = phone, password = password)
            new_user.save()
            return HttpResponse(status = 201)
        else:
            return HttpResponse(status = 406)
    return HttpResponse("Registration form", status = 401) #fix this

@csrf_exempt
def editUser(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        token = data.get('token')
        if token == None:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
        except ObjectDoesNotExist:
            return JsonResponse({
                'msg': "not_found"
            }, status = 404)

        if sessionTimeExpired(user.sessionExpTime):
            return JsonResponse({
                'msg': "session_time_expired"
            }, status = 404)

        if isCorruptedToken(req.GET['token'], user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)

        new_nick = user.nickName
        new_name = user.name
        new_password = user.password
        new_email = user.email
        if data.get('nickName'):
            new_nick = data.get('nickName')
        if data.get('name'):
            new_name = data.get('name')
        if data.get('password'):
            new_password = data.get('password')
        if data.get('email'):
            new_email = data.get('email')
        user.nickName = new_nick
        user.name = new_name
        user.password = new_password
        user.email = new_email
        user.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

def refreshToken(req):
    if req.method == 'GET':
        try:
            token = req.GET['token']
            refresh = req.GET['refresh']
            token_data = getDataFromToken(token)
        except:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
        except ObjectDoesNotExist:
            return JsonResponse({
                'msg': "not_found"
            }, status = 404)

        if isCorruptedToken(token, user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)

        if refresh == user.refresh:
            user.prv_key = random.randint(2**16, 2**32)
            user.sessionExpTime = int(time.time()) + sessionTime
            user.refresh = ''.join(random.choices(string.ascii_lowercase + string.digits, k=128))
            user.save()
            token = createSessionToken(token_data, user.prv_key)
            return JsonResponse({
                'response': {
                    'refresh': user.refresh,
                    'token': token
                },
            }, status = 200)
        else:
            return JsonResponse({
                'msg': 'refresh_not_match'
            }, status = 403)
    return HttpResponse(status = 405)