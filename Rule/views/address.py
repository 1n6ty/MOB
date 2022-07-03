from Rule.views.views import isCorruptedToken, getDataFromToken, createSessionToken, sessionTimeExpired
from django.views.decorators.csrf import csrf_exempt
from django.core.exceptions import ObjectDoesNotExist
from django.http import JsonResponse, HttpResponse, QueryDict
from Rule.models import User

def getLocations(req):
    if req.method == 'GET':
        try:
            token = req.GET['token']
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

        res = []
        addresses = user.addresses.all()
        for i in addresses:
            res.append({
                'country': i.country,
                'city': i.city,
                'district': i.district,
                'house': i.house,
                'id': i.id,
                'x': i.markx,
                'y': i.marky
            })
        return JsonResponse({
            'response': res
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def setLocation(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        new_location_id = data.get('location_id')
        token = data.get('token')
        if token == None or new_location_id == None:
            return JsonResponse({
                'msg': "bad_request"
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

        if isCorruptedToken(token, user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)
        token_data['location_id'] = new_location_id
    
        token = createSessionToken(token_data, user.prv_key)
        return JsonResponse({
            'response': {
                'token': token,
            }
        }, status = 200)
    return HttpResponse(status = 405)

def getMarks(req):
    if req.method == 'GET':
        try:
            token = req.GET['token']
            token_data = getDataFromToken(token)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
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

        res = {}
        for post in address.posts.all():
            res[post.id] = {
                'x': post.markx,
                'y': post.marky
            }

        return JsonResponse({
            "response": res
        }, status=200)
    return HttpResponse(status = 405)