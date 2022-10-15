import django.utils.timezone as time
from django.db import models

class Address(models.Model):
    country = models.CharField(max_length = 255, blank=False)
    city = models.CharField(max_length = 255, blank=False)
    street = models.CharField(max_length = 255, blank=False)
    house = models.CharField(max_length = 20, blank = False)
    posts = models.ManyToManyField('PostWithMark', blank = True)
    comments = models.ManyToManyField('Comment', blank=True)
    users = models.ManyToManyField('User', blank=True, related_name="users")
    owner = models.ForeignKey('User', on_delete=models.CASCADE, related_name="owner")

class User(models.Model):
    nick = models.CharField(max_length = 255, blank=False)
    profile_img = models.ImageField(upload_to = 'profiles', blank=False)
    full_name = models.CharField(max_length = 255, blank=False)
    email = models.EmailField(unique=True, blank=False)
    phone_number = models.CharField(max_length= 17, unique=True, blank=False)
    password = models.CharField(max_length = 255, blank=False)
    addresses = models.ManyToManyField('Address', blank = True)
    bio = models.CharField(max_length = 255, blank = True)
    prv_key = models.BigIntegerField(default=0)
    refresh = models.TextField(default='0')

class PostWithMark(models.Model):
    user = models.ForeignKey(User, on_delete = models.CASCADE)
    content = models.TextField(default='', blank=True)
    images = models.JSONField(default=dict([("images", list())]), blank=True)
    date = models.DateField(default=time.now())
    mark = models.JSONField(default=dict([('x', -1), ('y', -1)]))
    title = models.CharField(max_length=255, default="")
    rate = models.JSONField(default=dict([('p', list()), ('m', list())]))
    comments = models.ManyToManyField('Comment', blank=True)
    reactions = models.JSONField(default=dict(), blank=True)

class Comment(models.Model):
    user = models.ForeignKey(User, on_delete = models.CASCADE)
    content = models.TextField()
    date = models.DateField(default=time.now())
    rate = models.JSONField(default=dict([('p', list()), ('m', list())]))
    comments = models.ManyToManyField('Comment', blank=True)
    reactions = models.JSONField(default=dict(), blank=True)
