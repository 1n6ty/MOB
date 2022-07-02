from ast import Add
from django.contrib import admin
from .models import RuleCompany, Address, User, PostWithMark, Comment, Image

class RuleCompanyAdmin(admin.ModelAdmin):
    exclude = ()
    filter_horizontal = ('addresses', )

class AddressAdmin(admin.ModelAdmin):
    filter_horizontal = ('posts', )

class UserAdmin(admin.ModelAdmin):
    filter_horizontal = ('addresses', )

admin.site.register(RuleCompany, RuleCompanyAdmin)
admin.site.register(Address, AddressAdmin)
admin.site.register(User, UserAdmin)
admin.site.register(PostWithMark)
admin.site.register(Comment)
admin.site.register(Image)