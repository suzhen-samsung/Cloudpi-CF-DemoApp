#!/usr/bin/env python
import os
import sys
import settings
if __name__ == '__main__':
    os.environ.setdefault("DJANGO_SETTINGS_MODULE", "settings")
    sys.path.insert(0, os.path.join(settings.ROOT, "run_redisboard"))
    from django.core.management import execute_from_command_line
    if not os.path.exists('.redisboard.sqlite'):
        execute_from_command_line(['run_redisboard', 'syncdb', '--noinput'])
        from django.contrib.auth.models import User
        u = User.objects.create(username='cloudpiredis', is_superuser=True, is_staff=True, is_active=True)
        pwd = 'csq9hW76yrzSTJumP9up'
        u.set_password(pwd)
        u.save()
        print "="*80
        print """   Credentials:
            USERNAME: cloudpiredis
            PASSWORD: %s""" % pwd
        print "="*80
        from redisboard.models import RedisServer
        RedisServer.objects.create(label="ec2-54-87-17-26.compute-1.amazonaws.com", hostname="ec2-54-87-17-26.compute-1.amazonaws.com")
execute_from_command_line(sys.argv)
