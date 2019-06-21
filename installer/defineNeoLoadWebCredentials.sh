#!/bin/bash

YLW='\033[1;33m'
NC='\033[0m'

CREDS=./creds_nl.json
rm $CREDS 2> /dev/null

echo -e "${YLW}Please enter the credentials as requested below: ${NC}"
read -p "NeoLoad Web host Web URL (by default  neoload.saas.neotys.com  for SaaS (default=$NLWEB): " NLWEBC
read -p "NeoLoad Web host API URL (by default  neoload-api.saas.neotys.com  for SaaS (default=$NLWEBAPI): " NLWEBAPIC
read -p "NeoLoad Web host Upload URL (by default  neoload-files.saas.neotys.com  for SaaS (default=$NLWEBUPLOAD): " NLWEBUPLOADC
read -p "Neoload Web  API Token (default=$NLAPI): " NLAPIC
read -p "Neoload Web  ZONE ID (default=$NLZONE): " NLZONEC
echo ""

if [[ $NLWEB = '' ]]
then
    NLWEB=$NLWEBC
fi

if [[ $NLWEBAPI = '' ]]
then
    NLWEBAPI=$NLWEBAPIC
fi

if [[ $NLAPI = '' ]]
then
    NLAPI=$NLAPIC
fi

if [[ $NLZONE = '' ]]
then
    NLZONE=$NLZONEC
fi

if [[ $NLWEBUPLOAD = '' ]]
then
    NLWEBUPLOAD=$NLWEBUPLOADC
fi

echo ""
echo -e "${YLW}Please confirm all are correct: ${NC}"
echo "NL Web HOST WEB : $NLWEB"
echo "NL WEB API Host : $NLWEBAPI"
echo "NL WEB UPLOAD Host : $NLWEBUPLOAD"
echo "NL web API token: $NLAPI"
echo "NL web ZONE ID: $NLZONE"
read -p "Is this all correct? (y/n) : " -n 1 -r
echo ""

if [[ $REPLY =~ ^[Yy]$ ]]
then
    rm $CREDS 2> /dev/null
    cat ./creds_dt.sav | sed 's~NL_WEB_HOST_PLACEHOLDER~'"$NLWEB"'~' | \
      sed 's~NL_API_HOST_PLACEHOLDER~'"$NLWEBAPI"'~' | \
      sed 's~NL_UPLOAD_HOST_PLACEHOLDER~'"$NLWEBUPLOAD"'~' | \
      sed 's~NL_WEB_TOKEN~'"$NLAPI"'~'  | \
      sed 's~NL_WEB_ZONEID~'"$NLZONE"'~'>> $CREDS
fi

cat $CREDS
echo ""
echo "The credentials file can be found here:" $CREDS
echo ""