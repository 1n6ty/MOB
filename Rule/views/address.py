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

@csrf_exempt
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
                'mark': post.mark
            }

        return JsonResponse({
            "response": res
        }, status=200)
    return HttpResponse(status = 405)

@csrf_exempt
def createAddress(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        country = req.POST.get('country', False)
        city = req.POST.get('city', False)
        street = req.POST.get('street', False)
        house = req.POST.get('house', False)
        if not (token and country and city and street and house):
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
            
        # check token
        if sessionTimeExpired(token_data['expire_time']):
            return JsonResponse({
                'msg': "session_time_expired"
            }, status = 404)

        if isCorruptedToken(token, user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)

        new_address = Address.objects.create(country = country, city = city, street = street, house = house, owner = user)
        new_address.users.add(user)
        new_address.save()

        user.addresses.add(new_address)
        user.save()

        return JsonResponse({
            "response": {
                "id": new_address.id
            }
        }, status=200)
    return HttpResponse(status = 405)

@csrf_exempt
def joinAddress(req, id):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        id = id
        if not (token and id):
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
        address = Address.objects.filter(id = id).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "address_not_found"
            }, status = 404)
        
        # check token
        if sessionTimeExpired(token_data['expire_time']):
            return JsonResponse({
                'msg': "session_time_expired"
            }, status = 404)

        if isCorruptedToken(token, user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)

        if address.users.filter(id = user.id).count() == 0:
            user.addresses.add(address)
            address.users.add(user)

            user.save()
            address.save()

        return JsonResponse({
            "response": {
                "id": address.id
            }
        }, status=200)
    return HttpResponse(status = 405)

@csrf_exempt
def leaveAddress(req, id):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        id = id
        if not (token and id):
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
        address = Address.objects.filter(id = id).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "address_not_found"
            }, status = 404)
        
        # check token
        if sessionTimeExpired(token_data['expire_time']):
            return JsonResponse({
                'msg': "session_time_expired"
            }, status = 404)

        if isCorruptedToken(token, user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)

        if user.addresses.filter(id = address.id).count() > 0:
            user.addresses.remove(address)
            address.users.remove(user)

            if address.owner.id == user.id:
                address.delete()
            else:
                address.save()

            user.save()

        return JsonResponse({
            "response": {
                "id": address.id
            }
        }, status=200)
    return HttpResponse(status = 405)