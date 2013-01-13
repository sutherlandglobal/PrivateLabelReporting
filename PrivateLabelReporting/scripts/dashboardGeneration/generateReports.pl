#!/usr/bin/perl -w -I./

use Date::Manip;
use Text::CSV;
use Text::CSV_XS;

use Report;

use strict;

our $csv = Text::CSV->new();

$| = 1;

our $runDate = new Date::Manip::Date;
$runDate->parse("Now");

print "Running batch reports at: " . $runDate->printf("%Y-%m-%d %H:%M:%S") ."\n";
our $reportHour = $runDate->printf("%H");
our $reportMinute = $runDate->printf("%M");

our $birtHome = "/opt/tomcat/";
our $birtReportDir = $birtHome . "webapps/birt-viewer/";

my $forceRun = defined $ARGV[0] && $ARGV[0] eq '-f';

my $confDir = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/conf/dashboard";
my $targetDir = "/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting/dashboards/dump/";
my $deployDir = "/opt/tomcat/webapps/birt-viewer/dashboards";
		
my %generatedReports;

	#for each conf file
	#if process 
		#for each processed report
			#do any date formatting
			#if the report hasn't been run, run report, dump to dir

my $attemptedReports = 0;
my $runReports = 0;

foreach my $file (`ls $confDir`)
{
	next unless $file =~ /conf$/;
	
	chomp $file;
	
	print "conf file: $file\n";
	
	my ($email, @reports) = &processConf($confDir . "/" . $file);
	
	print "Found " . ($#reports + 1)  . " reports for " . $email . "\n";
	
	next unless $email ne "" && ($#reports + 1)  > 0;
	
	`mkdir $targetDir` unless -e $targetDir;
	`mkdir $deployDir` unless -e $deployDir;

	my $reportFreq;
	foreach my $report( @reports )
	{			
		$attemptedReports++;
			
		my $reportFile =  $generatedReports{$report->getReportFile() . "_" . $report->getType() . "_". $report->getArgString()};

		$reportFreq = $report->getFreq();

		#system call to generate report to one dir
		#if the report generation fails, the reportFile (outputFile) will be an empty string
			
		if
		( 
			(
				$forceRun || 
				(index($reportFreq, ":") == -1 && $reportMinute % $reportFreq == 0) ||
				(index($reportFreq, ":") != -1 && $reportFreq eq "$reportHour:$reportMinute")
			)
		)
		{	
			if(!$reportFile)
			{
				$reportFile = &generateReport($report, $targetDir);
				$runReports++;
			}
			else
			{
				print "Report $reportFile was already run, opting not to re-run\n";
			}

			print "got $reportFile from genRep\n";
			
			my $userDashboardDir = $deployDir . "/" . $email;
			my $userDashboardImageDir = $userDashboardDir . "/image/"; 
			
			`mkdir $userDashboardDir` unless -e $userDashboardDir;
			`mkdir $userDashboardImageDir` unless -e $userDashboardImageDir;
			
			#copy, since several users could request the same report
			print "Copying $targetDir$reportFile to $userDashboardDir\n";
			`cp -f \"$targetDir$reportFile\" $userDashboardDir`;
			
			#also figure out which images this report uses in dump/image and copy them to $userBlastDir/image
			
			#egrep -r "custom[0-9]+\.png" "dashboards/dump/DocumentationRate_AgentName=Thomas-Vail_Yesterday_201105311038.html" | awk -F\s\r\c\=\" '{print $2}' | awk -F\" '{print $1}'
			
			my $sysCall = 'egrep -r "custom[0-9]+\.png|file[0-9]+\.jpg" "' . $targetDir . $reportFile . '"  | awk -F\s\r\c\=\" \'{print $2}\' | awk -F\" \'{print $1}\' | uniq';
			
			my $output = `$sysCall`;
			
			#print "$sysCall returned: $output\n";
			
			foreach my $file( split(/\n/, $output))
			{
				chomp $file;
				#print $file . "\n";
				
				#copy instead of move, in case an image is used across several reports
				print "Copying $targetDir/$file to $userDashboardImageDir\n";
				`cp -f $targetDir/$file $userDashboardImageDir` unless $file eq "";
			}
			
			#something to prevent re-execution, people may use different titles to describe the same report
			$generatedReports{$report->getReportFile() . "_" . $report->getType() . "_" . $report->getArgString()} = $reportFile;
		}
		else
		{
			print "Skipping report, not time to run yet " . $report->getReportFile() . "\n";
		}
		
		print "==================\n";
	}
}
	
`rm -rf $targetDir`;
	
print "\n\nRan $runReports reports out of $attemptedReports reports requested\n";
print "==================\n";

sub processConf
{
	#retrieve the report + args
	
	my ($file) = $_[0];
	
	my $emailField = "email";
	my $reportField = "report";
	
	my $email;
	my $type;
	my $title;
	my $report;
	my $freq;
	my @reports;
	
	open IN, "<$file" or print "Could not read configDir: $!\n";
	
	foreach(<IN>)
	{
		
		
		if($_ !~ /^\s*$/ &&  $_ !~ /^#/ )
		{
			chomp;
			
			$title = "";
			
			
			my $field = (split(/\=/, $_ ))[0];
			my $val = substr($_, length($field) + 1);
			next unless $field && $val;
			
			if($field eq $emailField)
			{
				$email = $val unless $val !~ /[a-zA-Z0-9\.\_\-]/;
			}
			elsif($field eq $reportField)
			{
				print "val: $val\n";
				
				if($csv->parse($val))
				{
					$freq =  ($csv->fields())[0];
					
					my $reportPath = $birtReportDir . ($csv->fields())[1];
					
					
					$type = ($csv->fields())[2];
					
					$title = ($csv->fields())[3];
					
					
					
					my @valueFields = $csv->fields();
					my %args;
					my $arg;
					
					for(my $i = 4; $i< $#valueFields  ; $i++)
					{
						$arg = $valueFields[$i];
						#reportPath arg1 arg2 arg3
						
						print "handling arg: $arg\n";

						

						if($arg eq "Today")
						{
							my $date = new Date::Manip::Date;
   							$date->parse("today");
							
							my $startInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							my $delta =  $date->new_delta();
							$delta->parse("+24 hours");
							
							$date = $date->calc($delta);
							
							$delta->parse("-1 seconds");
							
							
							$date = $date->calc($delta);
							
							my $endInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							$args{"startDate"} =  $startInt;
							$args{"endDate"} =  $endInt;
							
							#print $startInt . " --> " . $endInt . "\n";
							
							#$title = $arg;
						}
						elsif($arg eq "Yesterday")
						{
							my $date = new Date::Manip::Date;
   							$date->parse("yesterday");
							
							my $startInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							my $delta =  $date->new_delta();
							$delta->parse("+24 hours");
							
							$date = $date->calc($delta);
							
							$delta->parse("-1 seconds");
							
							
							$date = $date->calc($delta);
							
							my $endInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							$args{"startDate"} =  $startInt;
							$args{"endDate"} =  $endInt;
							
							#print $startInt . " --> " . $endInt . "\n";
							
							#$title = $arg;
						}
						elsif($arg eq "This Week")
						{
							my $date = new Date::Manip::Date;
   							$date->parse("last monday");
							
							my $startInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							my $delta =  $date->new_delta();
							$delta->parse("+7 days");
							
							$date = $date->calc($delta);
							
							$delta->parse("-1 seconds");
							
							
							$date = $date->calc($delta);
							
							my $endInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							$args{"startDate"} =  $startInt;
							$args{"endDate"} =  $endInt;
							
							#print $startInt . " --> " . $endInt . "\n";
							
							#$title = $arg;
						}
						elsif($arg eq "Last Week")
						{
							my $date = new Date::Manip::Date;
   							$date->parse("last monday");
							
							my $delta =  $date->new_delta();
							$delta->parse("-7 days");
							
							$date = $date->calc($delta);
							
							my $startInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							$delta =  $date->new_delta();
							
							$delta->parse("+7 days");
							$date = $date->calc($delta);
							
							$delta->parse("-1 seconds");
							$date = $date->calc($delta);
							
							my $endInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							$args{"startDate"} =  $startInt;
							$args{"endDate"} =  $endInt;
							
							#print $startInt . " --> " . $endInt . "\n";
							
							#$title = $arg;
						}
						elsif($arg eq "This Month")
						{
							my $date = new Date::Manip::Date;
							
							my $thisYear = `date +\%Y`;
							chomp $thisYear;
							
							my $thisMonth = `date +\%m`;
							chomp $thisMonth;

							my $dateStr = $thisYear . $thisMonth . "01000000";

							
   							$date->parse($dateStr);
							
							my $startInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							my $delta =  $date->new_delta();
							$delta->parse("+1 months");
							
							$date = $date->calc($delta);
							
							$delta->parse("-1 seconds");
							
							$date = $date->calc($delta);
							
							my $endInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							$args{"startDate"} =  $startInt;
							$args{"endDate"} =  $endInt;
							
							#print $startInt . " --> " . $endInt . "\n";
							
							#$title = $arg;
						}
						elsif($arg eq "Last Month")
						{
							my $date = new Date::Manip::Date;
							
							my $thisYear = `date +\%Y`;
							chomp $thisYear;
							
							my $thisMonth = `date +\%m`;
							chomp $thisMonth;

							my $dateStr = $thisYear . $thisMonth . "01000000";

							
   							$date->parse($dateStr);
   							
   							my $delta =  $date->new_delta();
   							$delta->parse("-1 months");
							$date = $date->calc($delta);
							
							my $startInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							
							
							$delta->parse("+1 months");
							$date = $date->calc($delta);
							
							$delta->parse("-1 seconds");
							$date = $date->calc($delta);
							
							my $endInt = $date->printf("%Y-%m-%d %H:%M:%S");
							
							$args{"startDate"} =  $startInt;
							$args{"endDate"} =  $endInt;
							
							#print $startInt . " --> " . $endInt . "\n";
							
							#$title = $arg;
						}
						elsif($arg =~ /\=/)
						{
							#push @args, ($csv->fields())[$i];
							my ($argField) = (split(/\=/, $arg ))[0];
							my $argVal = substr($arg, length($argField) + 1 );
							
							if($argField ne "" && $argVal ne "")
							{
								$args{$argField} = $argVal;
							}
							

							
							
						}
						else
						{
							print "Malformed parameter: $arg\n";
						}
						
#						if($title eq "")
#						{
#							$title = $arg;
#						}
#						else
#						{
#							$title .= "_" . $arg;
#						}
#						
#						$title =~ s/\ /\-/g;
					}
					
					eval
					{
#						print "name: $reportPath\n";
#						print "email: $email\n";
#						print "type: $type\n";
						
						my $report = Report->new($reportPath, $email, $title, $freq, $type, \%args);
						
						#print $report->getArgString() . "\n";
						print $report->getOutputFile() . "\n";
						
						push @reports, $report; 
						
						1;
					} or do
					{
						print "Error building report: " . $@;
					};
					
		

				}
				else
				{
					print "failed csv parse\n";
				}
			}
			else
			{
				print "Unexpected field: $field\n";
			}
		}
		else
		{
			#print "Ignoring line $_";
		}
	}
	
	close IN;
	
	return ($email, @reports);
}

sub generateReport
{
	#handle the system call to the report generation
	#take report, output full path if successful

	
	my ($report, $targetDir) = @_;
	
	my $retval = "";
	
	my $type = $report->getType();
	
	if( $report ne "" && $type ne "")
	{
		
#		my $reportName = `echo awk '{print \$NF}' `;
		my $outfile = $report->getOutputFile();
		
		#./genReport.new.sh -m runrender -o output.pdf -f pdf ~/webapps/birt-viewer/MSAccess/Roster.rptdesign 
		#./genReport.new.sh -m runrender -o output.pdf -f pdf -p numCallDrivers=10 -p startDate="2010-12-01 00:00:00" -p endDate="2010-12-31 23:59:59" ~/webapps/birt-viewer/MSAccess/TopCallDrivers.rptdesign
		my $genReportScript = $birtHome . "ReportEngine/genReport.new.sh";
		
		
		my $syscall =  "env BIRT_HOME=$birtHome $genReportScript -m runrender -o \"$targetDir$outfile\" -f $type "  . $report->getArgString() ." " . $report->getReportFile();
		
		print $syscall . "\n";
		
		my $output = `$syscall`;
		
		#if the report runs 
		if(-e "$targetDir$outfile") 
		{
			$retval = $report->getOutputFile();
		}
		else
		{
			print "Outfile not created, report probably failed\n";
			print "Output: $output\n";
		}
	}
	
	return $retval;
}

