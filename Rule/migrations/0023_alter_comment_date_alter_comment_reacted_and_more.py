# Generated by Django 4.0.1 on 2022-07-03 13:47

import datetime
from django.db import migrations, models
from django.utils.timezone import utc


class Migration(migrations.Migration):

    dependencies = [
        ('Rule', '0022_alter_comment_date_alter_postwithmark_date'),
    ]

    operations = [
        migrations.AlterField(
            model_name='comment',
            name='date',
            field=models.DateField(default=datetime.datetime(2022, 7, 3, 13, 47, 34, 290331, tzinfo=utc)),
        ),
        migrations.AlterField(
            model_name='comment',
            name='reacted',
            field=models.JSONField(blank=True, default={}),
        ),
        migrations.AlterField(
            model_name='postwithmark',
            name='date',
            field=models.DateField(default=datetime.datetime(2022, 7, 3, 13, 47, 34, 290331, tzinfo=utc)),
        ),
        migrations.AlterField(
            model_name='postwithmark',
            name='reacted',
            field=models.JSONField(blank=True, default={}),
        ),
    ]