from django.urls import path

from Rule.views.address import getLocations, setLocation, getMarks
from Rule.views.comments import commentInc, commentDec, comment, deleteComment, reactComment, unreactComment, getComment, deleteComment
from Rule.views.posts import createPost, getPost, deletePost, postInc, postDec, reactPost, unreactPost
from Rule.views.user import auth, editUser, refreshToken

urlpatterns = [
    path('auth/', auth),
    path('getLocations/', getLocations),
    path('setLocation/', setLocation),
    path('createPost/', createPost),
    path('getMarks/', getMarks),
    path('getPost/', getPost),
    path('deletePost/', deletePost),
    path('editUser/', editUser),
    path('comment/', comment),
    path('getComment/', getComment),
    path('deleteComment/', deleteComment),
    path('postInc/', postInc),
    path('postDec/', postDec),
    path('commentInc/', commentInc),
    path('commentDec/', commentDec),
    path('postReact/', reactPost),
    path('postUnreact/', unreactPost),
    path('commentReact/', reactComment),
    path('commentUnreact/', unreactComment),
    path('refreshToken', refreshToken),
]