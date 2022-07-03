import hashlib
from django.shortcuts import render
from Rule.views.views import sessionTime, isCorruptedToken, getDataFromToken, createSessionToken, sessionTimeExpired
from django.views.decorators.csrf import csrf_exempt
from django.core.exceptions import ObjectDoesNotExist
from django.http import JsonResponse, HttpResponse, QueryDict
from django.db.models import Q
from Rule.models import User, Image
import random, time, string
from MOB.settings import BASE_DIR
from django.core.files import File

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
                'refresh': user.refresh,
                'user': {
                    'id': user.id,
                    'email': user.email,
                    'phone': user.phone,
                    'name': user.name,
                    'nick': user.nickName,
                    'profile_img_url': user.profile_img.url 
                }
            }
        }, status = 200)
    return HttpResponse(status = 405)

def register(req):
    if req.method == 'POST':
        try:
            nickName = req.POST['nick_name']
            password = req.POST['password']
            phone = req.POST['phone']
            email = req.POST['email']
            name = req.POST['name']
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        query = Q(phone_number__startswith = phone) | Q(email__startswith = email)
        user = User.objects.filter(query)
        
        if len(user) == 0:
            password = hashlib.sha256(str(password).encode('UTF-8')).hexdigest()

            new_user = User(nickName = nickName, name = name, email = email, phone_number = phone, password = password)
            profile = Image.objects.create(img = File(open(BASE_DIR / 'data/default_profile.jpg', 'rb'), 'profile_' + phone + '.jpg'))
            profile.save()
            new_user.profile_img = profile
            new_user.save()
            return render(req, 'registration.html', {
                "accept": True,
                'name': False,
                'nick': False,
                'err': False
            })
        else:
            return render(req, 'registration.html', {
                "accept": False,
                'err': True,
                'name': name,
                'nick': nickName
            })
    return render(req, 'registration.html', {
        "accept": False,
        'name': False,
        'nick': False,
        'err': False
    })

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
        try:
            user.profile_img.img = req.FILES.getlist('imgs')[0];
        except:
            pass
        user.nickName = new_nick
        user.name = new_name
        user.password = new_password
        if  len(User.objects.get(email = new_email)) == 0:
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