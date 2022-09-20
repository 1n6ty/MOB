# Generated by Django 4.0.2 on 2022-09-18 10:19

import datetime
from django.db import migrations, models
from django.utils.timezone import utc


class Migration(migrations.Migration):

    dependencies = [
        ('Rule', '0006_alter_comment_date_alter_postwithmark_date'),
    ]

    operations = [
        migrations.AlterField(
            model_name='comment',
            name='date',
            field=models.DateField(default=datetime.datetime(2022, 9, 18, 10, 19, 49, 770668, tzinfo=utc)),
        ),
        migrations.AlterField(
            model_name='postwithmark',
            name='date',
            field=models.DateField(default=datetime.datetime(2022, 9, 18, 10, 19, 49, 769668, tzinfo=utc)),
        ),
        migrations.AlterField(
            model_name='postwithmark',
            name='images',
            field=models.JSONField(blank=True, default={'images': []}),
        ),
    ]
