from itertools import chain
from MOB.settings import BASE_DIR, MEDIA_ROOT, MEDIA_URL
from Rule.views.views import isCorruptedToken, getDataFromToken, sessionTimeExpired
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse, HttpResponse
from Rule.models import Address, User, PostWithMark
import os
from django.core.files.storage import FileSystemStorage

fs = FileSystemStorage(location = MEDIA_ROOT + '/posts/')

def getPost(req):
    if req.method == 'GET':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        post_id = req.GET.get('post_id', False)
        if not (token and post_id):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        
        # get user and post objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data['location_id']).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_address_selected"
            }, status = 404)
        post = address.posts.filter(id = post_id).all()
        if post.count() > 0:
            post = post[0]
        else:
            return JsonResponse({
                'msg': "not_allowed"
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

        return JsonResponse({
            'response': {
                'date': str(post.date),
                'id': post.id,
                'user': {
                    'nick': post.user.nick,
                    'full_name': post.user.full_name,
                    'email': post.user.email,
                    'phone_number': post.user.phone_number,
                    'id': post.user.id,
                    'profile_img_url': post.user.profile_img.url
                },
                'data': {
                    'title': post.title,
                    'img_urls': post.images["images"],
                    'content': post.content,
                    'comment_ids': [i.id for i in post.comments.all()]
                },
                'reactions': post.reactions,
                'rate': post.rate
            }
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def deletePost(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        post_id = req.POST.get('post_id', False)
        if not (token and post_id):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        
        # get user, address and post objects 
        user = User.objects.filter(id = token_data['id']).all()
        address = Address.objects.filter(id = token_data['location_id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_address_selected"
            }, status = 404)
        post = address.posts.filter(id = post_id).all()
        if post.count() > 0:
            post = post[0]
        else:
            return JsonResponse({
                'msg': "not_allowed"
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
 
        if post.user.id == token_data['id']:
            comments2delete = list(post.comments.all())
            ind = 0
            while ind < len(comments2delete):
                if comments2delete[ind].comments.all().count() == 0:
                    ind += 1
                    continue
                comments2delete = list(chain(comments2delete, comments2delete[ind].comments.all()))
                ind += 1
            for i in comments2delete:
                i.delete()

            for i in post.images["images"]:
                os.remove(str(BASE_DIR) + i)
            post.delete()

            return JsonResponse({
                'response': {}
            }, status = 200)
        else:
            return JsonResponse({
                'msg': 'author_not_matched'
            }, status = 403)
    return HttpResponse(status = 405)

@csrf_exempt
def createPost(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        content = req.POST.get('content', False)
        title = req.POST.get('title', False)
        markx = req.POST.get('markx', False)
        marky = req.POST.get('marky', False)
        if not (token and content and title and markx and marky):
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
        address = Address.objects.filter(id = token_data['location_id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_address_selected"
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

        try:
            markx = float(req.POST['markx'])
            marky = float(req.POST['marky'])
            post = PostWithMark.objects.create(user = user, content = content, mark = {'x': markx, 'y': marky}, title = title)
        except KeyError:
            post = PostWithMark.objects.create(user = user, content = content, title = title)
        
        idF = len([name for name in os.listdir(MEDIA_ROOT + '/posts/')])
        post.images["images"] = []
        for f in req.FILES.getlist('images'):
            print(1)
            fs.save(f'post_{idF}.jpg', f);
            post.images["images"].append(str(MEDIA_URL) + f'posts/post_{idF}.jpg')
            idF += 1

        post.save()
        address.posts.add(post)
        address.save()

        return JsonResponse({
            'response': {
                'id': post.id
            }
        }, status=200)
    return HttpResponse(status = 405)

@csrf_exempt
def postInc(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        post_id = req.POST.get('post_id', False)
        if not (token and post_id):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, post and address objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data['location_id']).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_address_selected"
            }, status = 404)
        post = address.posts.filter(id = post_id).all()
        if post.count() > 0:
            post = post[0]
        else:
            return JsonResponse({
                'msg': "not_allowed"
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

        post.rate['p'] = list(chain(filter(lambda e: e != user.id, post.rate['p']), [user.id]))
        post.rate['m'] = list(filter(lambda e: e != user.id, post.rate['m']))
        post.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def postDec(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        post_id = req.POST.get('post_id', False)
        if not (token and post_id):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, post and address objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data['location_id']).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_address_selected"
            }, status = 404)
        post = address.posts.filter(id = post_id).all()
        if post.count() > 0:
            post = post[0]
        else:
            return JsonResponse({
                'msg': "not_allowed"
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

        post.rate['m'] = list(chain(filter(lambda e: e != user.id, post.rate['m']), [user.id]))
        post.rate['p'] = list(filter(lambda e: e != user.id, post.rate['p']))
        post.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def reactPost(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        post_id = req.POST.get('post_id', False)
        reaction = req.POST.get('reaction', False)
        if not (token and post_id and reaction):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, post and address objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data['location_id']).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_address_selected"
            }, status = 404)
        post = address.posts.filter(id = post_id).all()
        if post.count() > 0:
            post = post[0]
        else:
            return JsonResponse({
                'msg': "not_allowed"
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

        try:
            if len(list(filter(lambda x: x == user.id, post.reactions[reaction]))) == 0:
                post.reactions[reaction].append(user.id)
        except KeyError:
            post.reactions[reaction] = [user.id]
        post.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def unreactPost(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        post_id = req.POST.get('post_id', False)
        reaction = req.POST.get('reaction', False)
        if not (token and post_id and reaction):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, post and address objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data['location_id']).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_address_selected"
            }, status = 404)
        post = address.posts.filter(id = post_id).all()
        if post.count() > 0:
            post = post[0]
        else:
            return JsonResponse({
                'msg': "not_allowed"
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

        try:
            if len(list(filter(lambda x: x == user.id, post.reactions[reaction]))) != 0:
                cur = list(filter(lambda x: x != user.id, post.reactions[reaction]))
                if len(cur) == 0:
                    post.reactions.pop(reaction, None)
                else:
                    post.reactions[reaction] = cur
            post.save()
        except KeyError:
            return JsonResponse({
                'msg': "no_such_reaction"
            }, status = 404)

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)