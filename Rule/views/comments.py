from Rule.views.views import sessionTime, isCorruptedToken, getDataFromToken, sessionTimeExpired
from django.views.decorators.csrf import csrf_exempt
from django.core.exceptions import ObjectDoesNotExist
from django.http import JsonResponse, HttpResponse, QueryDict
from Rule.models import User, Comment
from datetime import datetime

sessionTime = 3600

def getComment(req):
    if req.method == 'GET':
        try:
            token = req.GET['token']
            post_id = req.GET['post_id']
            comment_id = req.GET['comment_id']
        except:
            return HttpResponse(status = 400)
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
            e = post.comments.all()[comment_id]
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


        
        appUsers = list(map(int, filter(lambda x: x != '' and x != ' ', e.appreciatedUsers.split(' '))))
        unappUsers = list(map(int, filter(lambda x: x != '' and x != ' ', e.unappreciatedUsers.split(' '))))
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
            new_react[k] = len(e.reacted[k])

        return JsonResponse({
            'response': {
                'user': {
                    'nickName': e.user.nickName,
                    'name': e.user.name,
                    'email': e.user.email,
                    'phone_number': e.user.phone_number,
                    'id': e.user.id
                },
                'id': e.id,
                'text': e.text,
                'date': str(e.date),
                'reactions': new_react,
                'appreciations': e.appreciations,
                'appreciated': app
            }
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def deleteComment(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        token = data.get('token')
        post_id = data.get('post_id')
        comment_id = data.get('comment_id')
        if token == None or post_id == None or comment_id == None:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(data.get('post_id'))
            comment_id = int(data.get('comment_id'))
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
            comment = post.comments.get(id = comment_id)
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

        if comment.user.id == token_data['id']:
            comment.delete()

            return JsonResponse({
                'response': {}
            }, status = 200)
        return JsonResponse({
            'msg': 'author_not_matched'
        }, status = 403)
    return HttpResponse(status = 405)

@csrf_exempt
def commentInc(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        token = data.get('token')
        post_id = data.get('post_id')
        comment_id = data.get('comment_id')
        if token == None or post_id == None or comment_id == None:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
            comment_id = int(comment_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
            comment = post.comments.get(id = comment_id)
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

        appUsers = list(map(int, filter(lambda x: x != '' and x != ' ', comment.appreciatedUsers.split(' '))))
        unappUsers = list(map(int, filter(lambda x: x != '' and x != ' ', comment.unappreciatedUsers.split(' '))))
        try:
            ind = appUsers.index(user.id)
        except:
            try:
                ind = unappUsers.index(user.id)
                comment.unappreciatedUsers = ' '.join(list(map(str, filter(lambda x: x != user.id, unappUsers))))
                comment.appreciatedUsers += ' ' + str(user.id)
                comment.appreciations += 2
                comment.save()
            except:
                comment.appreciatedUsers += ' ' + str(user.id)
                comment.appreciations += 1
                comment.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def commentDec(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        token = data.get('token')
        post_id = data.get('post_id')
        comment_id = data.get('comment_id')
        if token == None or post_id == None or comment_id == None:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
            comment_id = int(comment_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
            comment = post.comments.get(id = comment_id)
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

        appUsers = list(map(int, filter(lambda x: x != '' and x != ' ', comment.appreciatedUsers.split(' '))))
        unappUsers = list(map(int, filter(lambda x: x != '' and x != ' ', comment.unappreciatedUsers.split(' '))))
        try:
            ind = unappUsers.index(user.id)
        except:
            try:
                ind = appUsers.index(user.id)
                comment.appreciatedUsers = ' '.join(list(map(str, filter(lambda x: x != user.id, appUsers))))
                comment.unappreciatedUsers += ' ' + str(user.id)
                comment.appreciations -= 2
                comment.save()
            except:
                comment.unappreciatedUsers += ' ' + str(user.id)
                comment.appreciations -= 1
                comment.save()
        
        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 401)

@csrf_exempt
def comment(req):
    if req.method == 'POST':
        try:
            token = req.POST['token']
            post_id = req.POST['post_id']
            text = req.POST['text']
        except:
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

        new_c = Comment(user=user, text=text)
        new_c.save()
        post.comments.add(new_c)
        post.save()

        return JsonResponse({
            'response': {}
            }, status=200)
    return HttpResponse(status=405)

@csrf_exempt
def reactComment(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        token = data.get('token')
        post_id = data.get('post_id')
        comment_id = data.get('comment_id')
        reaction = data.get('reaction')
        if token == None or post_id == None or comment_id == None or reaction == None:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
            comment_id = int(comment_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
            comment = post.comments.get(id = comment_id)
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

        try:
            if len(list(filter(lambda x: x == user.id, comment.reacted[reaction]))) == 0:
                comment.reacted[reaction].append(user.id)
        except:
            comment.reacted[reaction] = [user.id]
        comment.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def unreactComment(req):
    if req.method == 'PUT':
        data = QueryDict(req.body)
        token = data.get('token')
        post_id = data.get('post_id')
        comment_id = data.get('comment_id')
        reaction = data.get('reaction')
        if token == None or post_id == None or comment_id == None or reaction == None:
            return JsonResponse({
                'msg': 'bad_request'
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            post_id = int(post_id)
            comment_id = int(comment_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        try:
            user = User.objects.get(id = token_data['id'])
            address = user.addresses.get(id = token_data['location_id'])
            post = address.posts.get(id = post_id)
            comment = post.comments.get(id = comment_id)
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

        if len(list(filter(lambda x: x == user.id, comment.reacted[reaction]))) != 0:
            comment.reacted[reaction] = list(filter(lambda x: x != user.id, comment.reacted[reaction]))
        comment.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)