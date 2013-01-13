#!/usr/bin/perl -w

use strict;

package Report;

sub new
{
	my $class = shift; 
	
	
	#string name
	#string email
	#string type
	#hash args name=>val
	my $self = 
	{
		_name => shift,
		_email => shift,
		_title => shift,
		_freq => shift,
		_type => shift,
		_args => shift,
		_outFile => "",
	};
	
	#args 
	
	if
	(
		$self->{_name} !~ /\.rptdesign$/ ||
		#!(-e $self->{_name}) || 
		$self->{_name} !~ /[a-zA-Z0-9\.\/\-\_]+/ ||
		(
			$self->{_type} ne "pdf" && $self->{_type} ne "html"
		) || 
		$self->{_email} !~ /[a-zA-Z0-9\.]+/ ||
		$self->{_title} eq ""
	)
	{
		print "Name: " . $self->{_name} . "\n";
		print "Email: " . $self->{_email} . "\n";
		print "Title: " . $self->{_title} . "\n";
		print "Freq: " . $self->{_freq} . "\n";
		print "Type: " . $self->{_type} . "\n";
		print "args: " . $self->{_args} . "\n";
		
		die "Malformed report parameters.";
	}
	
	bless $self, $class;
	return $self;
}

sub getFreq
{
	my($self) = shift;
	
	return $self->{_freq};
}

sub getArgString
{
	 my($self) = shift;
	 my $retval = "";
	 
	 #order is not preserved from file, that should not matter
	 
	 my $ref = $self->{_args};
	 
	 foreach my $key (keys ( %{$ref} ) )
	 {
	 	$retval .= " -p \"$key=" . $self->{_args}{$key} . "\"";
	 }
	 
	 return $retval;
}

sub getOutputFile
{
	my($self) = shift;
	
	#name_arg1_arg2_arg3_date
	
	#should really only generate this once.
	
	if($self->{_outFile} eq "")
	{
		#existence guaranteed by constructor
		my $reportName = `basename $self->{_name}`;
		chomp $reportName;

		my $outFile = substr($reportName, 0, index($reportName, ".rptdesign"));
		
		
		
#		my $date = `date +\%Y\%m\%d\%H\%M`;
#		chomp $date;
		
#		foreach my $arg (keys %{$self->{_args}} )
#		{
#			$arg =~ s/\ /\-/g;
#			
#			$outFile .=  "_" . $self->{_args}{$arg};
#		}

		my $title = $self->{_title};
		
		$title =~ s/\ /\_/g;
		$title =~ s/[^a-zA-Z0-9\.\-\_\,]//g;
		
		$outFile .= "_" . $title;# . "_" . $date;
		
		#type checked in constructor
		if($self->{_type} eq "pdf")
		{
			$outFile .= ".pdf";
		}
		elsif($self->{_type} eq "html")
		{
			$outFile .= ".html";
		}
		
		 $self->{_outFile} = $outFile;
		 
	}
	
	return  $self->{_outFile};
}

sub getReportFile
{
	my($self) = shift;
	
	return $self->{_name} ;
	
}

sub getType
{
	my($self) = shift;
	
	return $self->{_type};
}

sub getEmail
{
	my($self) = shift;
	
	return $self->{_email};
}

1;