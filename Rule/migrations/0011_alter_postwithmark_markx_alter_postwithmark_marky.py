# Generated by Django 4.0.2 on 2022-04-28 14:00

from django.db import migrations, models


class Migration(migrations.Migration):

    dependencies = [
        ('Rule', '0010_remove_comment_reactions_and_more'),
    ]

    operations = [
        migrations.AlterField(
            model_name='postwithmark',
            name='markx',
            field=models.FloatField(default=-1),
        ),
        migrations.AlterField(
            model_name='postwithmark',
            name='marky',
            field=models.FloatField(default=-1),
        ),
    ]