from django.contrib import admin
from .models import Address, User, PostWithMark, Comment

class AddressAdmin(admin.ModelAdmin):
    filter_horizontal = ('posts', )

class UserAdmin(admin.ModelAdmin):
    filter_horizontal = ('addresses', )

admin.site.register(Address, AddressAdmin)
admin.site.register(User, UserAdmin)
admin.site.register(PostWithMark)
admin.site.register(Comment)