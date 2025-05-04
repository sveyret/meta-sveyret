SUMMARY = "Temurin JRE Binaries"

JAVA_TYPE = "jre"
JAVA_SHA256:x86-64 = "6d48379e00d47e6fdd417e96421e973898ac90765ea8ff2d09ae0af6d5d6a1c6"

require temurin.inc

S = "${WORKDIR}/jdk-${PV}-jre"

inherit update-alternatives

# Lower than corresponding JDK
ALTERNATIVE_PRIORITY = "90"
ALTERNATIVE:${PN} = "java keytool"

ALTERNATIVE_LINK_NAME[java] = "${bindir}/java"
ALTERNATIVE_TARGET[java] = "${JAVA_HOME}/bin/java"

ALTERNATIVE_LINK_NAME[keytool] = "${bindir}/keytool"
ALTERNATIVE_TARGET[keytool] = "${JAVA_HOME}/bin/keytool"
