from itertools import chain
from datetime import datetime
from Rule.views.views import isCorruptedToken, getDataFromToken, removeCommas, sessionTimeExpired
from django.views.decorators.csrf import csrf_exempt
from django.http import JsonResponse, HttpResponse
from Rule.models import User, Comment, Address

@csrf_exempt
def getComment(req):
    if req.method == 'GET':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        comment_id = req.GET.get('comment_id', False)
        if not (token and comment_id):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            comment_id = int(comment_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user and comment objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data["location_id"]).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_such_address"
            }, status = 404)
        comment = address.comments.filter(id = comment_id).all()
        if comment.count() > 0:
            comment = comment[0]
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

        return JsonResponse({
            'response': {
                'user': {
                    'nick_name': comment.user.nick,
                    'full_name': comment.user.full_name,
                    'email': comment.user.email,
                    'phone_number': comment.user.phone_number,
                    'id': comment.user.id,
                    'profile_img_url': comment.user.profile_img.url
                },
                'data': {
                    'id': comment.id,
                    'content': comment.content,
                    'comment_ids': [i.id for i in comment.comments.all()],
                    'public_date': comment.date.strftime('%d.%m.%Y/%H:%M'),
                },
                'reactions': comment.reactions,
                'rate': comment.rate
            }
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def deleteComment(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        comment_id = req.POST.get('comment_id', False)
        if not (token and comment_id):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            comment_id = int(comment_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, address and comment objects 
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

        address = Address.objects.filter(id = token_data["location_id"]).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_such_address"
            }, status = 404)
        comment = address.comments.filter(id = comment_id).all()
        if comment.count() > 0:
            comment = comment[0]
            comments2delete = [comment]
            ind = 0
            while ind < len(comments2delete):
                if comments2delete[ind].comments.all().count() == 0:
                    ind += 1
                    continue
                comments2delete = list(chain(comments2delete, comments2delete[ind].comments.all()))
                ind += 1
            for i in comments2delete:
                i.delete()
        else:
            return JsonResponse({
                'msg': "no_such_comment"
            }, status = 404)

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def commentInc(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        comment_id = req.POST.get('comment_id', False)
        if not (token and comment_id):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            comment_id = int(comment_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, address and comment objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data["location_id"]).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_such_address"
            }, status = 404)
        comment = address.comments.filter(id = comment_id).all()
        if comment.count() > 0:
            comment = comment[0]
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

        if user.id in comment.rate['p']:
            comment.rate['p'] = list(filter(lambda e: e != user.id, comment.rate['p']))
        else:
            comment.rate['p'] = list(chain(filter(lambda e: e != user.id, comment.rate['p']), [user.id]))
            comment.rate['m'] = list(filter(lambda e: e != user.id, comment.rate['m']))
        comment.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def commentDec(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        comment_id = req.POST.get('comment_id', False)
        if not (token and comment_id):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            comment_id = int(comment_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, address and comment objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data["location_id"]).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_such_address"
            }, status = 404)
        comment = address.comments.filter(id = comment_id).all()
        if comment.count() > 0:
            comment = comment[0]
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
        
        if user.id in comment.rate['m']:
            comment.rate['m'] = list(filter(lambda e: e != user.id, comment.rate['m']))
        else:
            comment.rate['m'] = list(chain(filter(lambda e: e != user.id, comment.rate['m']), [user.id]))
            comment.rate['p'] = list(filter(lambda e: e != user.id, comment.rate['p']))
        comment.save()
        
        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 401)

@csrf_exempt
def comment(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        parent_id = req.POST.get('parent_id', False)
        parent_litera = req.POST.get('l', False)
        content = req.POST.get('content', False)
        if not (token and parent_id and content and parent_litera) and (parent_litera == 'c' or parent_litera == 'p'):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            parent_id = int(parent_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, address and comment objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data["location_id"]).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_such_address"
            }, status = 404)
        print(parent_id, parent_litera)
        parent = address.comments.filter(id = parent_id).all() if parent_litera == 'c' else address.posts.filter(id = parent_id).all()
        if parent.count() > 0:
            parent = parent[0]
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

        new_c = Comment(user=user, content=removeCommas(content), date = datetime.now())
        new_c.save()
        parent.comments.add(new_c)
        parent.save()
        address.comments.add(new_c)
        address.save()

        return JsonResponse({
            'response': {
                "id": new_c.id
            }
            }, status=200)
    return HttpResponse(status=405)

@csrf_exempt
def reactComment(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        comment_id = req.POST.get('comment_id', False)
        reaction = req.POST.get('reaction', False)
        if not (token and comment_id and reaction):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            comment_id = int(comment_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, address and comment objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data["location_id"]).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_such_address"
            }, status = 404)
        comment = address.comments.filter(id = comment_id).all()
        if comment.count() > 0:
            comment = comment[0]
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

        try:
            if len(list(filter(lambda x: x == user.id, comment.reactions[reaction]))) == 0:
                comment.reactions[reaction].append(user.id)
        except KeyError:
            comment.reactions[reaction] = [user.id]
        comment.save()

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)

@csrf_exempt
def unreactComment(req):
    if req.method == 'POST':
        # get request parameters
        token = req.META.get('HTTP_AUTHORIZATION', False)
        comment_id = req.POST.get('comment_id', False)
        reaction = req.POST.get('reaction', False)
        if not (token and comment_id and reaction):
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)
        try:
            token_data = getDataFromToken(token)
            comment_id = int(comment_id)
        except:
            return JsonResponse({
                'msg': "bad_request"
            }, status = 400)

        # get user, address and comment objects 
        user = User.objects.filter(id = token_data['id']).all()
        if user.count() > 0:
            user = user[0]
        else:
            return JsonResponse({
                'msg': "user_not_found"
            }, status = 404)
        address = Address.objects.filter(id = token_data["location_id"]).all()
        if address.count() > 0:
            address = address[0]
        else:
            return JsonResponse({
                'msg': "no_such_address"
            }, status = 404)
        comment = address.comments.filter(id = comment_id).all()
        if comment.count() > 0:
            comment = comment[0]
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

        try:
            if len(list(filter(lambda x: x == user.id, comment.reactions[reaction]))) != 0:
                cur = list(filter(lambda x: x != user.id, comment.reactions[reaction]))
                if len(cur) == 0:
                    comment.reactions.pop(reaction, None)
                else:
                    comment.reactions[reaction] = cur
            comment.save()
        except KeyError:
            return JsonResponse({
                'msg': "no_such_reaction"
            }, status = 404)

        return JsonResponse({
            'response': {}
        }, status = 200)
    return HttpResponse(status = 405)
