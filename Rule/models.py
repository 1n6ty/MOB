import django.utils.timezone as time
from django.db import models

class RuleCompany(models.Model):
    name = models.CharField(max_length = 255)
    addresses = models.ManyToManyField('Address', blank = True)

class Address(models.Model):
    country = models.CharField(max_length = 255)
    city = models.CharField(max_length = 255)
    district = models.CharField(max_length = 255)
    house = models.PositiveIntegerField()
    posts = models.ManyToManyField('PostWithMark', blank = True)
    markx = models.FloatField(default=-1, blank=False)
    marky = models.FloatField(default=-1, blank=False)

class User(models.Model):
    nickName = models.CharField(max_length = 255)
    profile_img = models.ImageField(upload_to = 'profiles')
    name = models.CharField(max_length = 255)
    email = models.EmailField(unique=True)
    phone_number = models.CharField(max_length= 17, unique=True)
    password = models.CharField(max_length = 255)
    addresses = models.ManyToManyField('Address', blank = True)
    sessionExpTime = models.BigIntegerField(default=0)
    prv_key = models.BigIntegerField(default=0)
    refresh = models.TextField(default='0')

class PostWithMark(models.Model):
    user = models.ForeignKey(User, on_delete = models.CASCADE)
    text = models.TextField()
    imgs = models.TextField(default='', blank=True)
    date = models.DateField(default=time.now())
    markx = models.FloatField(default=-1)
    marky = models.FloatField(default=-1)
    title = models.CharField(max_length=255, default="")
    appreciations = models.IntegerField(default=0)
    appreciatedUsers = models.TextField(blank=True)
    unappreciatedUsers = models.TextField(blank=True)
    comments = models.ManyToManyField('Comment', blank=True)
    reacted = models.JSONField(default={}, blank=True)

class Comment(models.Model):
    user = models.ForeignKey(User, on_delete = models.CASCADE)
    text = models.TextField()
    appreciations = models.IntegerField(default=0)
    appreciatedUsers = models.TextField(blank=True)
    unappreciatedUsers = models.TextField(blank=True)
    date = models.DateField(default=time.now())
    reacted = models.JSONField(default={}, blank=True)

    class Meta:
        ordering = ['-appreciations', '-date']