import hashlib
import os
from django.shortcuts import render
from Rule.views.views import sessionTime, isCorruptedToken, getDataFromToken, createSessionToken, sessionTimeExpired
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse, HttpResponse
from django.db.models import Q
from Rule.models import User
import random, time, string
from MOB.settings import BASE_DIR
from django.core.files import File

def auth(req):
    if req.method == 'GET':
        # get request parameters
        login = req.GET.get('l', False)
        password = req.GET.get('p', False)
        if not (login and password):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        password = hashlib.sha256(str(password).encode('UTF-8')).hexdigest()

        # get user objects 
        user = User.objects.filter(Q(phone_number = login) | Q(email = login) | Q(nick = login), password = password).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)

        # setting up session
        user.prv_key = random.randint(2**16, 2**32)
        user.refresh = ''.join(random.choices(string.ascii_lowercase + string.digits, k=128))
        user.save()

        return JsonResponse({
            'response': {
                'token': createSessionToken({
                    'id': user.id,
                    'expire_time': int(time.time()) + sessionTime,
                    'location_id': -1
                }, user.prv_key),
                'refresh': user.refresh,
                'user': {
                    'id': user.id,
                    'email': user.email,
                    'phone_number': user.phone_number,
                    'full_name': user.full_name,
                    'nick': user.nick,
                    'profile_img_url': user.profile_img.url 
                }
            }
        }, status = 200)

    return HttpResponse(status = 405)

@csrf_exempt
def getUserProfile(req):
    if req.method == 'GET':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        user_id = req.GET.get('user_id', False)
        if not (token and user_id):
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user and profile objects
        user = User.objects.filter(id = token_data['id']).all()
        user_profile = User.objects.filter(id = user_id).all()
        if user.count() < 1:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        else:
            user = user[0]
        if user_profile.count() < 1:
            return JsonResponse({
                'msg': "profile_not_found"
            }, status = 404)
        else:
            user_profile = user_profile[0]

        # check token
        if sessionTimeExpired(token_data['expire_time']):
            return JsonResponse({
                'msg': "session_time_expired"
            }, status = 404)

        if isCorruptedToken(token, user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)

        return JsonResponse({
            'response': {
                'user': {
                    'id': user_profile.id,
                    'nick': user_profile.nick,
                    'full_name': user_profile.full_name,
                    'profile_img_url': user_profile.profile_img.url
                }
            }
        }, status = 200)

    return HttpResponse(status = 405)

@csrf_exempt
def getMe(req):
    if req.method == 'GET':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        if not (token):
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, current_address and address objects
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        current_address = user.addresses.filter(id = token_data['location_id']).all()
        if current_address.count() > 0:
            current_address = current_address[0]
        else:
            return JsonResponse({
                'msg': "no_such_address"
            }, status = 404)

        addresses = user.addresses.all()
        if addresses.count() == 0:
            addresses = 'none'

        # check token
        if sessionTimeExpired(token_data['expire_time']):
            return JsonResponse({
                'msg': "session_time_expired"
            }, status = 404)

        if isCorruptedToken(token, user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)

        return JsonResponse({
            'response': {
                'user': {
                    'nick': user.nick,
                    'full_name': user.full_name,
                    'email': user.email,
                    'phone_number': user.phone_number,
                    'id': user.id,
                    'profile_img_url': user.profile_img.url
                },
                'current_address': current_address if current_address == 'none' else {
                    'country': current_address.country,
                    'city': current_address.city,
                    'street': current_address.street,
                    'house': current_address.house
                },
                'addresses': addresses if addresses == 'none' else [{
                    'country': i.country,
                    'city': i.city,
                    'street': i.street,
                    'house': i.house
                } for i in addresses]
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

            new_user = User(nick = nickName, full_name = name, email = email, phone_number = phone, password = password)
            new_user.save()
            new_user.profile_img = File(open(BASE_DIR / 'data/default_profile.jpg', 'rb'), 'profile_' + str(new_user.id) + '.jpg')
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
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        if not (token):
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user object
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

        # edit user
        new_nick = req.POST.get('nick', False)
        new_full_name = req.POST.get('full_name', False)
        new_password = req.POST.get('password', False)
        new_email = req.POST.get('email', False)
        new_phone_number = req.POST.get('phone_number', False)
        if new_nick:
            user.nick = new_nick
        if new_full_name:
            user.full_name = new_full_name
        if new_password:
            user.password = hashlib.sha256(str(new_password).encode('UTF-8')).hexdigest()
        if new_email:
            if user.email != new_email and User.objects.filter(email = new_email).all().count() == 0:
                user.email = new_email
            else:
                return JsonResponse({
                    'msg': "email_already_exist"
                }, status = 403)
        if new_phone_number:
            if user.phone_number != new_phone_number and User.objects.filter(phone_number = new_phone_number).all().count() == 0:
                user.phone_number = new_phone_number
            else:
                return JsonResponse({
                    'msg': "phone_number_already_exist"
                }, status = 403)
        if req.FILES.get('new_profile_img', False):
            os.remove(str(BASE_DIR) + user.profile_img.url)
            user.profile_img = File(req.FILES['new_profile_img'], 'profile_' + str(user.id) + '.jpg')
        user.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

def refreshToken(req):
    if req.method == 'GET':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        refresh = req.GET.get('refresh', False)
        if not (token and refresh):
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user object
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
            
        # check token
        if isCorruptedToken(token, user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)

        if refresh == user.refresh:
            user.prv_key = random.randint(2**16, 2**32)
            user.refresh = ''.join(random.choices(string.ascii_lowercase + string.digits, k=128))
            user.save()
            token_data['expire_time'] = int(time.time()) + sessionTime
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