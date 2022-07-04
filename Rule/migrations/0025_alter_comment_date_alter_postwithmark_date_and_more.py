# Generated by Django 4.0.1 on 2022-07-04 11:00

import datetime
from django.db import migrations, models
from django.utils.timezone import utc


class Migration(migrations.Migration):

    dependencies = [
        ('Rule', '0024_address_markx_address_marky_alter_comment_date_and_more'),
    ]

    operations = [
        migrations.AlterField(
            model_name='comment',
            name='date',
            field=models.DateField(default=datetime.datetime(2022, 7, 4, 11, 0, 30, 178350, tzinfo=utc)),
        ),
        migrations.AlterField(
            model_name='postwithmark',
            name='date',
            field=models.DateField(default=datetime.datetime(2022, 7, 4, 11, 0, 30, 178350, tzinfo=utc)),
        ),
        migrations.AlterField(
            model_name='user',
            name='nickName',
            field=models.CharField(max_length=255),
        ),
    ]