from django.urls import path

from Rule.views.address import createAddress, joinAddress, leaveAddress, setLocation, getMarks
from Rule.views.comments import commentInc, commentDec, comment, deleteComment, reactComment, unreactComment, getComment, deleteComment
from Rule.views.posts import createPost, getPost, deletePost, postInc, postDec, reactPost, unreactPost
from Rule.views.user import auth, editUser, getMe, refreshToken, register, getUserProfile

urlpatterns = [
    path('user/auth/', auth),
    path('user/register/', register),
    path('user/get/', getUserProfile),
    path('user/edit/', editUser),
    path('user/refresh/', refreshToken),
    path('user/me/', getMe),

    path('address/set/', setLocation),
    path('address/marks/get/', getMarks),
    path('address/create/', createAddress),
    path('address/join/<int:id>/', joinAddress),
    path('address/leave/<int:id>/', leaveAddress),

    path('post/create/', createPost),
    path('post/get/', getPost),
    path('post/delete/', deletePost),
    path('post/rate/increment/', postInc),
    path('post/rate/decrement/', postDec),
    path('post/reactions/add/', reactPost),
    path('post/reactions/remove/', unreactPost),

    path('comment/create/', comment),
    path('comment/get/', getComment),
    path('comment/delete/', deleteComment),
    path('comment/rate/increment/', commentInc),
    path('comment/rate/decrement/', commentDec),
    path('comment/reactions/add/', reactComment),
    path('comment/reactions/remove/', unreactComment),
]