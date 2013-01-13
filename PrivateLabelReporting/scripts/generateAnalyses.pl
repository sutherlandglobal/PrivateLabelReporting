#!/usr/bin/perl -w

use strict;

my $path = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios/bin/";
my $classDir = "test/analysis/";

my $javaPath = "/usr/lib/jvm/java/bin/java";
my $targetDir = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios/analysis/";

my $classPath = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios.jar:/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/jackcess-1.2.1.jar";

my $gcDir = "gc/";
my $profDir = "profiles/";


foreach my $class (&buildClassList)
{
	
	$class =~ s/\.class$//;
	$class = substr($class, length($path));
	
	$class =~ s/\//\./g;
	
	print $class . "\n";
	
	runGCAnalysis($class);
	runProfAnalysis($class);
}


sub buildClassList
{
	my @classList = split(/\n/,`find $path$classDir -name *.class`);

	return @classList;
}

sub runGCAnalysis
{
	my $class = $_[0];
	#java classpath options -jar jarfile class  
	
	my $date = `date +%s`;
	chomp $date;
	
	my $targetFile = $targetDir . $gcDir . $class . "." . $date . ".gcinfo";
	
	my $sysCall = "$javaPath -cp $classPath -Xloggc:$targetFile -XX:+PrintGCDetails $class";
	
	#print $sysCall . "\n";
	
	`$sysCall`;
}

sub runProfAnalysis
{
		my $class = $_[0];
	#java classpath options -jar jarfile class  
	
	my $date = `date +%s`;
	chomp $date;
		
	my $targetFile = $targetDir . $profDir . $class . "." . $date . ".hprof";
	
	my $sysCall = "$javaPath -cp $classPath -Xrunhprof:cpu=samples,depth=6,thread=y,file=$targetFile $class";
	
	#print $sysCall . "\n";
	
	`$sysCall`;
}