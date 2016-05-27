#!/bin/bash -e
# this script needs hardening!!!


DIST_FILE=$1
CONFIG_LAYER=$2

HYBRIS_ROOT_DIR="/opt/hybris"
DIST_HYBRIS_VERSION=`echo ${DIST_FILE}| awk -F '-' '{print $2}'`;
BRANCH=`echo ${DIST_FILE}| awk -F '-' '{print $5}'`;

#Stop nicely, any existing hybris instances
for dir in `ls -1 ${HYBRIS_ROOT_DIR}`; do
    pushd ${HYBRIS_ROOT_DIR}
	if [ -d $dir/hybris/bin/platform ]; then
	  pushd $dir/hybris/bin/platform
            
	    ./hybrisserver.sh stop
	  popd
	fi
    popd
done

#remove the previous installed version and reextract it.
pushd "$HYBRIS_ROOT_DIR"
  if [ -d ${HYBRIS_ROOT_DIR} ]; then
    rm -rf ${HYBRIS_ROOT_DIR}/hybris-commerce-suite-*
  fi
  
  mkdir -p ${HYBRIS_ROOT_DIR}/hybris-commerce-suite-${DIST_HYBRIS_VERSION}/hybris
  pushd ${HYBRIS_ROOT_DIR}/hybris-commerce-suite-${DIST_HYBRIS_VERSION}
    pushd hybris
      unzip -q /misc/e2y-dists/ypay/${BRANCH}/${DIST_FILE}
      cp -p  deployment_config/config_${CONFIG_LAYER}/local.properties  config/local.properties
      cp -p  deployment_config/config_${CONFIG_LAYER}/merchants.xml  bin/ext-worldpay/worldpayapi/resources/merchants/.
      rm -rf deployment_config
      pushd bin/platform
        . ./setantenv.sh
        ant all
      popd
	popd
  popd
popd

