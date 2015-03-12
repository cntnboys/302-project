# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import uuidfield.fields


class Migration(migrations.Migration):

    dependencies = [
    ]

    operations = [
        migrations.CreateModel(
            name='Authors',
            fields=[
                ('author_id', models.AutoField(serialize=False, primary_key=True)),
                ('author_uuid', uuidfield.fields.UUIDField(unique=True, max_length=32, editable=False, blank=True)),
                ('name', models.CharField(max_length=200)),
                ('username', models.CharField(unique=True, max_length=30)),
                ('image', models.ImageField(max_length=250, null=True, upload_to=b'ProfileImages')),
                ('email', models.EmailField(unique=True, max_length=80)),
                ('location', models.CharField(max_length=200)),
                ('github', models.CharField(max_length=200, null=True)),
                ('twitter', models.CharField(max_length=200, null=True)),
                ('facebook', models.CharField(max_length=200, null=True)),
            ],
            options={
                'db_table': 'authors',
                'verbose_name': 'Author',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Comments',
            fields=[
                ('comment_id', models.AutoField(serialize=False, primary_key=True)),
                ('comment_uuid', uuidfield.fields.UUIDField(unique=True, max_length=32, editable=False, blank=True)),
                ('date', models.DateTimeField(verbose_name=b'date posted')),
                ('content', models.CharField(max_length=2000, null=True)),
                ('image', models.ImageField(max_length=250, null=True, upload_to=b'ProfileImages')),
                ('author_id', models.ForeignKey(to='main.Authors', db_column=b'author_id')),
            ],
            options={
                'db_table': 'comments',
                'verbose_name': 'Comment',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='FacebookStreams',
            fields=[
                ('fbstream_id', models.AutoField(serialize=False, primary_key=True)),
                ('fbstream_uuid', uuidfield.fields.UUIDField(unique=True, max_length=32, editable=False, blank=True)),
                ('date', models.DateTimeField(verbose_name=b'date posted')),
                ('content', models.CharField(max_length=10000)),
                ('author_id', models.ForeignKey(to='main.Authors', db_column=b'author_id')),
            ],
            options={
                'db_table': 'facebookstreams',
                'verbose_name': 'FacebookStream',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Friends',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('status', models.BooleanField(default=False)),
                ('invitee_id', models.ForeignKey(related_name='invitee_id', to='main.Authors')),
                ('inviter_id', models.ForeignKey(related_name='inviter_id', to='main.Authors')),
            ],
            options={
                'db_table': 'friends',
                'verbose_name': 'Friend',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='GithubStreams',
            fields=[
                ('gh_id', models.AutoField(serialize=False, primary_key=True)),
                ('gh_uuid', uuidfield.fields.UUIDField(unique=True, max_length=32, editable=False, blank=True)),
                ('date', models.DateTimeField(verbose_name=b'date posted')),
                ('content', models.CharField(max_length=10000)),
                ('author_id', models.ForeignKey(to='main.Authors', db_column=b'author_id')),
            ],
            options={
                'db_table': 'githubstreams',
                'verbose_name': 'GithubStream',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='Posts',
            fields=[
                ('post_id', models.AutoField(serialize=False, primary_key=True)),
                ('post_uuid', uuidfield.fields.UUIDField(unique=True, max_length=32, editable=False, blank=True)),
                ('title', models.CharField(max_length=300)),
                ('content', models.CharField(max_length=10000)),
                ('image', models.ImageField(max_length=250, null=True, upload_to=b'PostImages')),
                ('privacy', models.CharField(max_length=20)),
                ('date', models.DateTimeField(auto_now_add=True)),
                ('author_id', models.ForeignKey(to='main.Authors', db_column=b'author_id')),
            ],
            options={
                'db_table': 'posts',
                'verbose_name': 'Post',
            },
            bases=(models.Model,),
        ),
        migrations.CreateModel(
            name='TwitterStreams',
            fields=[
                ('tweet_id', models.AutoField(serialize=False, primary_key=True)),
                ('tweet_uuid', uuidfield.fields.UUIDField(unique=True, max_length=32, editable=False, blank=True)),
                ('date', models.DateTimeField(verbose_name=b'date posted')),
                ('content', models.CharField(max_length=10000)),
                ('author_id', models.ForeignKey(to='main.Authors', db_column=b'author_id')),
            ],
            options={
                'db_table': 'twitterstreams',
                'verbose_name': 'TwitterStream',
            },
            bases=(models.Model,),
        ),
        migrations.AlterUniqueTogether(
            name='friends',
            unique_together=set([('inviter_id', 'invitee_id')]),
        ),
        migrations.AddField(
            model_name='comments',
            name='post_id',
            field=models.ForeignKey(to='main.Posts', db_column=b'post_id'),
            preserve_default=True,
        ),
    ]
