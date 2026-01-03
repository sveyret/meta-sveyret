SUMMARY = "Temurin JRE Binaries"

JAVA_TYPE = "jre"
JAVA_SHA256:x86-64 = "aeab55d064a1a27a3744b0880b9b414077b4ed2b1790817eea3df60aec946431"

require temurin.inc

S = "${UNPACKDIR}/jdk-${PV}-jre"

inherit update-alternatives

# Lower than corresponding JDK
ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE:${PN} = "java keytool"

ALTERNATIVE_LINK_NAME[java] = "${bindir}/java"
ALTERNATIVE_TARGET[java] = "${JAVA_HOME}/bin/java"

ALTERNATIVE_LINK_NAME[keytool] = "${bindir}/keytool"
ALTERNATIVE_TARGET[keytool] = "${JAVA_HOME}/bin/keytool"
