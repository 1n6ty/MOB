# Generated by Django 4.0.2 on 2022-04-29 13:54

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Rule', '0011_alter_postwithmark_markx_alter_postwithmark_marky'),
    ]

    operations = [
        migrations.AddField(
            model_name='user',
            name='prv_key',
            field=models.BigIntegerField(default=0),
        ),
        migrations.AddField(
            model_name='user',
            name='sessionExpTime',
            field=models.BigIntegerField(default=0),
        ),
    ]
