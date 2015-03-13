# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Patient',
            fields=[
                ('patient_id', models.IntegerField(serialize=False, primary_key=True)),
                ('ahcn', models.CharField(max_length=9, unique=True, null=True)),
                ('dob', models.DateField()),
                ('liveStatus', models.BooleanField(default=False)),
                ('doctor', models.CharField(max_length=5, null=True)),
                ('name', models.CharField(max_length=10, null=True)),
            ],
            options={
                'db_table': 'patients',
                'verbose_name': 'Patient',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='ECG',
            fields=[
                ('patient_id', models.ForeignKey(primary_key=True, db_column=b'patient_id', serialize=False, to='main.Patient')),
                ('mv', models.IntegerField()),
                ('pulse', models.IntegerField()),
                ('oxygen', models.IntegerField()),
                ('diastolicbp', models.IntegerField()),
                ('systolicbp', models.IntegerField()),
                ('map', models.IntegerField()),
                ('timestamp', models.DateTimeField(auto_now_add=True)),
                ('session_id', models.IntegerField(unique=True)),
                ('deviceType', models.CharField(max_length=10, null=True)),
            ],
            options={
                'db_table': 'ecg',
                'verbose_name': 'ECG',
            },
            bases=(models.Model,),
        ),
    ]
