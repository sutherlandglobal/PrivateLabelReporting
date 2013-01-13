#!/usr/bin/perl -w

use strict;

#cronjob to check if tomcat is running, start it if not
#consider that tomcat may already be starting up or shutting down as part of a deployment push

#while this is a helios cronjob, it's up to the implementer (privatelabel, etc) to schedule it in their crontab

my $envPrefix = 'env JAVA_HOME=/usr/lib/jvm/java/';
my $startUpScript = '/opt/tomcat/bin/startup.sh';
my $shutdownScript = '/opt/tomcat/bin/shutdown.sh';
my $thisScriptName = 'tomcatWatchdog.pl';
my $tomcatPSCmd = 'ps -ef | grep tomcat | grep java | grep -v grep | grep catalina';

#check that the watchdog proc isn't already doing something
#check that this script isn't being executed with a different pid -> already running

die "Watchdog process already running. Exiting\n" unless !`ps -ef | grep $thisScriptName | grep -v grep | grep -v "$$"`;

#tomcat shell scripts override the watchdog -- startup.sh and shutdown.sh
die "Tomcat already starting up. Exiting\n" unless !`ps -ef | grep $startUpScript | grep -v grep`;
die "Tomcat already shutting down. Exiting\n" unless !`ps -ef | grep $shutdownScript | grep -v grep`;

#is tomcat running?
if(!`$tomcatPSCmd`)
{
	#start tomcat by calling the startup script
	print `$envPrefix $startUpScript`;
	
	#print when the watchdog kicked in
	print "Watchdog restarted tomcat";
	
	sleep 5;
	
	if(`$tomcatPSCmd`)
	{
		print " and succeeded\n";
	}
	else
	{
		print " and failed\n";
	}
}
