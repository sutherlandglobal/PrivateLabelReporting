#!/usr/bin/perl -w

use strict;


#1. mount share if not already mounted (ls /mnt/share does not exist)
#2. compare target and local with diff
#3. if different, copy from share, append timestamp
#4. check lsof, update symlink

#\\rocjfsnas05\ROCUSRStore4\users\jcroffe\AnswersBy\ezCLM_Database\db\Jason\ezCLM Database Info_be.accdb


#by default, root can only run mount/umount operations, so either sort out permissions (probably the best) or run as root

my $fileName = "ezCLM Database Info_be.accdb";
my $localPath = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios/";
my $localFile = $localPath . "ezCLM_Database_current.accdb";
my $mountPath = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios/share/";
my $mountFile = $mountPath . $fileName;

my $remotePath = "\\\\\\\\rocjfsnas05\\\\ROCUSRStore4\\\\users\\\\jcroffe\\\\AnswersBy\\\\ezCLM_Database\\\\db\\\\Jason";

my $MAX_COPIES = 	5;

#mount if not mounted
#samba mount
die "Failed mounting share\n" unless &mountShare($remotePath, $mountFile, $mountPath);

print "smbmount worked\n";

our $MAX_ATTEMPTS = 20;

#wait for no writes to db
#a report will open a connection, run queries over time, then close the connection
my $attempts = 0;
while(  (`/usr/sbin/lsof $localFile` ne "" || `ls -a $mountPath | grep \.lck` ne "" ) && $attempts < $MAX_ATTEMPTS)
{
	$attempts++;
	print "Waiting\n";
	sleep 2;
}

die "Could not get a lock on the local db\n" unless ($attempts < $MAX_ATTEMPTS);

#print `rsync -v -u $mountFile $localFile`;


#check for difference

$mountFile =~ s/\ /\\ /g;
$fileName =~ s/\ /\\ /g;

if(`diff -q $localFile $mountFile`)
{
	my $suffix = `date +"_\%Y_\%m_\%d_\%H_\%M_\%S"`;
	chomp $suffix;

	print "Updated DB Found, copying...";
	print `cp $mountFile $localPath$fileName$suffix`;
	print "done.\n";
	 
	print `ln -fs $localPath$fileName$suffix $localFile`;
	
	#remove the oldest, leaving MAX remaining
	
	while(`ls -L $localPath | grep \.accdb_[2-9][0-9][0-9][0-9]_[0-9][0-9]_[0-9][0-9]_[0-9][0-9]_[0-9][0-9]_[0-9][0-9]\$ |  wc -l` > $MAX_COPIES)
	{
		#delete the oldest file
		my $oldest = `ls -Lt $localPath | grep \.accdb_[2-9][0-9][0-9][0-9]_[0-9][0-9]_[0-9][0-9]_[0-9][0-9]_[0-9][0-9]_[0-9][0-9]\$ | tail -1 `;
		
		die "Error determining oldest file\n" unless $oldest;
		
		chomp $oldest;
		print "Deleting old DB: $oldest\n";
		$oldest = $localPath . $oldest;
		$oldest =~ s/\ /\\ /g;
		print `rm $oldest`;
	}
}
else
{
	print "skipping\n";
}


sub mountShare
{
	my ($remotePath, $mountFile, $mountPath) = @_;
	my $retval = 1;
	
	#check if mounted
	
	if(! (-e $mountFile) )
	{
		#mount
		#smbmount \\\\192.168.1.137\\pub /mnt/pub
		
		#print `/usr/bin/smbmount \\\\\\\\$remoteHost\\\\$remotePath $mountPath -o guest --verbose`;
		print `/sbin/mount.cifs $remotePath $mountPath -o user=sgl\\\\jadiamon%WARh32d33333 --verbose`;
		$retval = 0 unless (-e $mountFile);
	}
	
	#if not mounted, mount and check again for success
	
	return $retval;
}