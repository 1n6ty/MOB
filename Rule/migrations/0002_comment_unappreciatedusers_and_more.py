# Generated by Django 4.0.2 on 2022-04-15 16:18

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Rule', '0001_initial'),
    ]

    operations = [
        migrations.AddField(
            model_name='comment',
            name='unappreciatedUsers',
            field=models.TextField(blank=True),
        ),
        migrations.AddField(
            model_name='postwithmark',
            name='unappreciatedUsers',
            field=models.TextField(blank=True),
        ),
    ]