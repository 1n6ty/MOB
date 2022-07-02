from Rule.views.views import isCorruptedToken, getDataFromToken, sessionTimeExpired
from django.views.decorators.csrf import csrf_exempt
from django.core.exceptions import ObjectDoesNotExist
from django.http import JsonResponse, HttpResponse, QueryDict
from Rule.models import User, PostWithMark
import os
from django.conf import settings

def getPost(req):
    if req.method == 'GET':
        try:
            token = req.GET['token']
            token_data = getDataFromToken(token)
            post_id = int(req.GET['post_id'])
        except:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
        except (ObjectDoesNotExist, IndexError) as e:
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



        appUsers = list(map(int, filter(lambda x: x != '' and x != ' ', post.appreciatedUsers.split(' '))))
        unappUsers = list(map(int, filter(lambda x: x != '' and x != ' ', post.unappreciatedUsers.split(' '))))
        app = -1
        try:
            appUsers.index(user.id)
            app = True
        except:
            pass
        try:
            unappUsers.index(user.id)
            app = False
        except:
            pass
        new_react = {}
        for k in post.reacted:
            new_react[k] = len(post.reacted[k])

        imgs = [i.url for i in post.imgs.all()]
        res = {
            'date': str(post.date),
            'id': post.id,
            'user': {
                'nick_name': post.user.nickName,
                'name': post.user.name,
                'email': post.user.email,
                'phone_number': post.user.phone_number,
                'id': post.user.id
            },
            'data': {
                'img_urls': imgs,
                'text': post.text
            },
            'reactions': new_react,
            'appreciations': post.appreciations,
            'appreciated': app
        }
        
        return JsonResponse({
            'response': res
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def deletePost(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        post_id = data.get('post_id')
        token = data.get('token')

        if token == None or post_id == None:
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
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
        except (ObjectDoesNotExist, IndexError) as e:
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
 
        if post.user.id == token_data['id']:
            for i in post.comments.all():
                i.delete()
            for i in post.imgs.all():
                os.remove(settings.MEDIA_ROOT + i.name)
                i.delete()
            post.delete()

            return JsonResponse({
                'response': {}
            }, status = 200)
        return JsonResponse({
            'msg': 'author_not_matched'
        }, status = 403)
    return HttpResponse(status = 405)

@csrf_exempt
def createPost(req):
    if req.method == 'POST':
        print(req.POST)
        try:
            token = str(req.POST['token']).replace("\"", "")
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            text = str(req.POST['text']).replace("\"", "")
        except:
            text = ""
        try:
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

        if isCorruptedToken(token, user.prv_key):
            return JsonResponse({
                'msg': "token_corrupted"
            }, status = 403)

        try:
            markx = float(req.POST['markx'])
            marky = float(req.POST['marky'])
            post = PostWithMark(user = user, text = text, markx = markx, marky = marky)
        except KeyError:
            post = PostWithMark(user = user, text = text)
        post.save()
        
        for f in req.FILES.getlist('imgs'):
            post.imgs.create(img = f)

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
    if req.method == 'PUT':
        data = QueryDict(req.body)
        token = data.get('token')
        post_id = data.get('post_id')
        if token == None or post_id == None:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
        except (ObjectDoesNotExist, IndexError) as e:
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

        appUsers = list(map(int, filter(lambda x: x != '' and x != ' ', post.appreciatedUsers.split(' '))))
        unappUsers = list(map(int, filter(lambda x: x != '' and x != ' ', post.unappreciatedUsers.split(' '))))
        try:
            ind = appUsers.index(user.id)
        except:
            try:
                ind = unappUsers.index(user.id)
                post.unappreciatedUsers = ' '.join(list(map(str, filter(lambda x: x != user.id, unappUsers))))
                post.appreciatedUsers += ' ' + str(user.id)
                post.appreciations += 2
                post.save()
            except:
                post.appreciatedUsers += ' ' + str(user.id)
                post.appreciations += 1
                post.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def postDec(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        token = data.get('token')
        post_id = data.get('post_id')
        if token == None or post_id == None:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
        except (ObjectDoesNotExist, IndexError) as e:
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

        appUsers = list(map(int, filter(lambda x: x != '' and x != ' ', post.appreciatedUsers.split(' '))))
        unappUsers = list(map(int, filter(lambda x: x != '' and x != ' ', post.unappreciatedUsers.split(' '))))
        try:
            ind = unappUsers.index(user.id)
        except:
            try:
                ind = appUsers.index(user.id)
                post.appreciatedUsers = ' '.join(list(map(str, filter(lambda x: x != user.id, appUsers))))
                post.unappreciatedUsers += ' ' + str(user.id)
                post.appreciations -= 2
                post.save()
            except:
                post.unappreciatedUsers += ' ' + str(user.id)
                post.appreciations -= 1
                post.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def reactPost(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        token = data.get('token')
        post_id = data.get('post_id')
        reaction = data.get('reaction')
        if token == None or post_id == None or reaction == None:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
        except (ObjectDoesNotExist, IndexError) as e:
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

        if isCorruptedToken(token, user.prv_key) or sessionTimeExpired(user.sessionExpTime):
            return HttpResponse(status = 403)

        try:
            if len(list(filter(lambda x: x == user.id, post.reacted[reaction]))) == 0:
                post.reacted[reaction].append(user.id)
        except:
            post.reacted[reaction] = [user.id]
        post.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def unreactPost(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        token = data.get('token')
        post_id = data.get('post_id')
        reaction = data.get('reaction')
        if token == None or post_id == None or reaction == None:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
        except (ObjectDoesNotExist, IndexError) as e:
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

        if isCorruptedToken(token, user.prv_key) or sessionTimeExpired(user.sessionExpTime):
            return HttpResponse(status = 403)

        if len(list(filter(lambda x: x == user.id, post.reacted[reaction]))) != 0:
            post.reacted[reaction] = list(filter(lambda x: x != user.id, post.reacted[reaction]))
        post.save()

        return JsonResponse({
            'token': token,
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)