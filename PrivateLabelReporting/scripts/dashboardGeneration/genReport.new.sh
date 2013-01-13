################USAGE OF REPORTRUNNER#########################
# echo "org.eclipse.birt.report.engine.impl.ReportRunner Usage:";
# echo "--mode/-m [ run | render | runrender] the default is runrender "
# echo "for runrender mode: "
# echo "" "we should add it in the end <design file> "
# echo "" "--format/-f [ HTML | PDF ] "
# echo "" "--output/-o <target file>"
# echo "" "--htmlType/-t < HTML | ReportletNoCSS >"
# echo "" "--locale /-l<locale>"
# echo "" "--parameter/-p <"parameterName=parameterValue">"
# echo "" "--file/-F <parameter file>"
# echo "" "--encoding/-e <target encoding>"
# echo " "
# echo "Locale: default is english"
# echo "parameters in command line will overide parameters in parameter file"
# echo "parameter name cant include characters such as \ ', '=', ':'"
# echo " "
# echo "For RUN mode:"
# echo "we should add it in the end<design file>"
# echo "" "--output/-o <target file>"
# echo "" "--locale /-l<locale>"
# echo "" "--parameter/-p <parameterName=parameterValue>"
# echo "" "--file/-F <parameter file>"
# echo " "
# echo "Locale: default is english"
# echo "parameters in command line will overide parameters in parameter file"
# echo "parameter name cant include characters such as \ ', '=', ':'"
# echo " "
# echo "For RENDER mode:"
# echo "" "we should add it in the end<design file>"
# echo "" "--output/-o <target file>"
# echo "" "--page/-p <pageNumber>"
# echo "" "--locale /-l<locale>"
# echo " "
# echo "Locale: default is english"
################END OF USAGE #########################
if [ "$BIRT_HOME" = "" ];

then
echo " The BIRT_HOME need be set before BirtRunner can run.";
else


java_io_tmpdir=$BIRT_HOME/ReportEngine/tmpdir
org_eclipse_datatools_workspacepath=$java_io_tmpdir/workspace_dtp
mkdir -p $org_eclipse_datatools_workspacepath
export BIRTCLASSPATH="$BIRT_HOME/ReportEngine/lib/com.ibm.icu_4.4.2.v20110208.jar:$BIRT_HOME/ReportEngine/lib/com.lowagie.text_2.1.7.v201004222200.jar:$BIRT_HOME/ReportEngine/lib/commons-cli-1.0.jar:$BIRT_HOME/ReportEngine/lib/derby.jar:$BIRT_HOME/ReportEngine/lib/flute.jar:$BIRT_HOME/ReportEngine/lib/javax.wsdl_1.5.1.v201012040544.jar:$BIRT_HOME/ReportEngine/lib/js.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.bridge_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.css_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.dom_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.dom.svg_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.ext.awt_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.parser_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.pdf_1.6.0.v201105071520.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.svggen_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.transcoder_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.util_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.util.gui_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.batik.xml_1.6.0.v201011041432.jar:$BIRT_HOME/ReportEngine/lib/org.apache.commons.codec_1.3.0.v201101211617.jar:$BIRT_HOME/ReportEngine/lib/org.apache.xerces_2.9.0.v201101211617.jar:$BIRT_HOME/ReportEngine/lib/org.apache.xml.resolver_1.2.0.v201005080400.jar:$BIRT_HOME/ReportEngine/lib/org.apache.xml.serializer_2.7.1.v201005080400.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.birt.runtime_3.7.1.v20110913-1734.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.core.contenttype_3.4.100.v20110423-0524.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.core.expressions_3.4.300.v20110228.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.core.filesystem_1.3.100.v20110423-0524.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.core.jobs_3.5.100.v20110404.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.core.resources_3.7.100.v20110510-0712.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.core.runtime_3.7.0.v20110110.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity_1.2.3.v201108101135.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.apache.derby_1.0.102.v201107130538.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.apache.derby.dbdefinition_1.0.2.v200906161815.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.console.profile_1.0.0.v200906020553.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.dbdefinition.genericJDBC_1.0.1.v200906161815.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.db.generic_1.0.1.v200908130547.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.oda_3.3.2.v201105200920.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.oda.consumer_3.2.4.v201105200848.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.oda.design_3.3.3.v201105191315.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.oda.flatfile_3.1.1.v201108260633.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.oda.profile_3.2.6.v201108060848.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.connectivity.sqm.core_1.2.3.v201109022323.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.hsqldb_1.0.0.v200906020900.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.hsqldb.dbdefinition_1.0.0.v200906161800.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.ibm.db2.luw_1.0.2.v201005211230.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.ibm.db2.luw.dbdefinition_1.0.4.v201005211215.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.ibm.informix_1.0.1.v201005211230.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.ibm.informix.dbdefinition_1.0.4.v201005211230.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.msft.sqlserver_1.0.1.v201001180222.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.msft.sqlserver.dbdefinition_1.0.0.v201004211630.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.mysql_1.0.2.v201109022323.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.mysql.dbdefinition_1.0.4.v200906161800.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.oda.ws_1.2.2.v201106161731.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.oda.xml_1.2.1.v201104121500.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.oracle_1.0.0.v200908130544.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.oracle.dbdefinition_1.0.102.v201101120745.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.postgresql_1.0.1.v200906020900.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.enablement.postgresql.dbdefinition_1.0.1.v200906161800.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.modelbase.dbdefinition_1.0.2.v201002130228.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.modelbase.derby_1.0.0.v201002161452.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.modelbase.sql_1.0.4.v201002250945.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.datatools.modelbase.sql.query_1.1.1.v201008100700.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.emf_2.6.0.v20110905-0916.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.emf.common_2.7.0.v20110905-0902.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.emf.ecore_2.7.0.v20110905-0902.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.emf.ecore.change_2.7.1.v20110829-1916.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.emf.ecore.xmi_2.7.0.v20110520-1406.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.equinox.app_1.3.100.v20110321.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.equinox.common_3.6.0.v20110523.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.equinox.preferences_3.4.1.R37x_v20110725.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.equinox.registry_3.5.101.R37x_v20110810-1611.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.osgi_3.7.1.R37x_v20110808-1106.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.osgi.services_3.3.0.v20110513.jar:$BIRT_HOME/ReportEngine/lib/org.eclipse.update.configurator_3.3.100.v20100512.jar:$BIRT_HOME/ReportEngine/lib/org.w3c.css.sac_1.3.0.v200805290154.jar:$BIRT_HOME/ReportEngine/lib/org.w3c.dom.smil_1.0.0.v200806040011.jar:$BIRT_HOME/ReportEngine/lib/org.w3c.dom.svg_1.1.0.v201011041433.jar:$BIRT_HOME/ReportEngine/lib/Tidy.jar"

JAVACMD='java';
$JAVACMD -cp "$BIRTCLASSPATH:/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/Helios.jar:/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/PrivateLabelReporting.jar:/opt/tomcat/webapps/birt-viewer/WEB-INF/lib/jackcess-1.2.1.jar" -DBIRT_HOME="$BIRT_HOME/ReportEngine" -Dorg.eclipse.datatools_workspacepath="$org_eclipse_datatools_workspacepath" org.eclipse.birt.report.engine.api.ReportRunner ${1+"$@"}

fi
