# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('main', '0001_initial'),
    ]

    operations = [
        migrations.RenameField(
            model_name='ecg',
            old_name='map',
            new_name='map2',
        ),
        migrations.RemoveField(
            model_name='ecg',
            name='deviceType',
        ),
    ]
