#!/usr/bin/perl -w

use strict;

$| = 1;

our $MAX_ATTEMPTS = 20;
our $attempts = 0;
#our $dbFile = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios/ezCLM_Database_current.accdb";

#retval of 0 indicates to the ant builder that things are good. retval of !0 fails the build
our $retval =0;

#wait on db inactivity, possibly timing out, then restart tomcat
#inactivity = no open handles, and no lock files.

print "Restarting tomcat...\n";

#while( (`/usr/sbin/lsof $dbFile` || `/bin/ls /opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios/ | /bin/grep \.lck\$`) && $attempts < $MAX_ATTEMPTS)
#{
#	print "Waiting, database in use\n";
#	sleep 3;
#	$attempts++;
#}

if($attempts < $MAX_ATTEMPTS)
{
	if(`ps -ef | grep java | grep catalina | grep -v grep`)
	{
		print "Shutting down tomcat\n";
		print `/opt/tomcat/apache-tomee-plus-1.5.2/bin/shutdown.sh`;
		
		while(`ps -ef | grep java | grep catalina | grep -v grep`)
		{
			sleep 2;
		}
	}
	
	print "Starting up tomcat\n";
	print `/opt/tomcat/apache-tomee-plus-1.5.2/bin/startup.sh`;
	
	sleep 12;
	
	print "DONE\n";
}
else
{
	print "FAILED, timeout waiting for database availability\n";
	$retval = 1;
}

exit $retval;