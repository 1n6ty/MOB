# Generated by Django 4.0.1 on 2022-07-03 19:08

import datetime
from django.db import migrations, models
from django.utils.timezone import utc


class Migration(migrations.Migration):

    dependencies = [
        ('Rule', '0023_alter_comment_date_alter_comment_reacted_and_more'),
    ]

    operations = [
        migrations.AddField(
            model_name='address',
            name='markx',
            field=models.FloatField(default=-1),
        ),
        migrations.AddField(
            model_name='address',
            name='marky',
            field=models.FloatField(default=-1),
        ),
        migrations.AlterField(
            model_name='comment',
            name='date',
            field=models.DateField(default=datetime.datetime(2022, 7, 3, 19, 8, 21, 143172, tzinfo=utc)),
        ),
        migrations.AlterField(
            model_name='postwithmark',
            name='date',
            field=models.DateField(default=datetime.datetime(2022, 7, 3, 19, 8, 21, 135154, tzinfo=utc)),
        ),
    ]
