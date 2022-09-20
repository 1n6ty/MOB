from Rule.views.views import isCorruptedToken, getDataFromToken, createSessionToken, sessionTimeExpired
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse, HttpResponse
from Rule.models import Address, User


@csrf_exempt
def setLocation(req):
    if req.method == 'POST':
        # get request parameters
        new_location_id = req.POST.get('location_id', False)
        token = req.META.get('HTTP_AUTHORIZATION', False)
        if not (token and new_location_id):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user and address objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id=new_location_id, user=user).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "not_allowed"
            }, status = 403)
            
        # check token
        if sessionTimeExpired(token_data['expire_time']):
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
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        if not (token):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user and address objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id=token_data['location_id'], user=user).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "not_allowed"
            }, status = 403)
            
        # check token
        if sessionTimeExpired(token_data['expire_time']):
            return JsonResponse({
                'msg': "session_time_expired"
            }, status = 404)

        if isCorruptedToken(token, user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)

        res = {}
        for post in address.posts.all():
            res[post.id] = {
                'mark': post.mark,
                'category': post.category
            }

        return JsonResponse({
            "response": res
        }, status=200)
    return HttpResponse(status = 405)