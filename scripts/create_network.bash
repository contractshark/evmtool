#!/bin/bash
# SPDX-License-Identifier: ISC
# 
# IBFT 2.0 with permissions IBFT2 w/ setup script. 
# 4 node setup
# besu is built using `gradlew installDist
#

SCRIPTPATH="$( cd "$(dirname "$0")" ; pwd -P )"
BESU_PATH="${SCRIPTPATH}/besu/build/install/besu"
NETWORK="${SCRIPTPATH}/IBFT-NETWORK"
NODE_COUNT='4'
 
#Create node directories
 for((i=1;i<=$NODE_COUNT;i++))
do
    mkdir -p "${NETWORK}/Node-${i}/data" 
done
 
# Create IBFT2 Genesis file
cat > $NETWORK/ibftConfigFile.json << IBFT_CONFIG
{
 "genesis": {
   "config": {
      "chainId": 2018,
      "constantinoplefixblock": 0,
      "ibft2": {
        "blockperiodseconds": 2,
        "epochlength": 30000,
        "requesttimeoutseconds": 10
      }
    },
    "nonce": "0x0",
    "timestamp": "0x58ee40ba",
    "gasLimit": "0x47b760",
    "difficulty": "0x1",
    "mixHash": "0x63746963616c2062797a616e74696e65206661756c7420746f6c6572616e6365",
    "coinbase": "0x0000000000000000000000000000000000000000",
    "alloc": {
       "fe3b557e8fb62b89f4916b721be55ceb828dbd73": {
          "privateKey": "8f2a55949038a9610f50fb23b5883af3b4ecb3c3bb792cbcefbd1542c692be63",
          "comment": "private key and this comment are ignored.  In a real chain, the private key should NOT be stored",
          "balance": "0xad78ebc5ac6200000"
       },
       "627306090abaB3A6e1400e9345bC60c78a8BEf57": {
         "privateKey": "c87509a1c067bbde78beb793e6fa76530b6382a4c0241e5e4a9ec0a0f44dc0d3",
         "comment": "private key and this comment are ignored.  In a real chain, the private key should NOT be stored",
         "balance": "90000000000000000000000"
       },
       "f17f52151EbEF6C7334FAD080c5704D77216b732": {
         "privateKey": "ae6ae8e5ccbfb04590405997ee2d52d2b330726137b875053c36d94e974d162f",
         "comment": "private key and this comment are ignored.  In a real chain, the private key should NOT be stored",
         "balance": "90000000000000000000000"
       }
	  }
 },
 "blockchain": {
   "nodes": {
     "generate": true,
       "count": $NODE_COUNT
   }
 }
}
IBFT_CONFIG
 
$BESU_PATH/bin/besu --version
$BESU_PATH/bin/besu operator generate-blockchain-config --config-file=$NETWORK/ibftConfigFile.json --to=$NETWORK/networkFiles
cp $NETWORK/networkFiles/genesis.json $NETWORK/
 
 
#Read the generated keys directories in an array
array=()
while IFS=  read -r -d $'\0'; do
    array+=("$REPLY")
done < <(find $NETWORK/networkFiles/keys -maxdepth 1 -mindepth 1 -type d -print0)
 
# Construct parameter values
PPPORT=30303
RPCPORT=8545
declare -a ENODES
declare -a PP_PORTS
declare -a RPC_PORTS
 
 for((i=0;i<${#array[@]};i++))
do
        NODE_KEY=`cat ${array[i]}/key.pub | cut -c3-`
        PP_PORTS[i]=$((PPPORT + i))
        RPC_PORTS[i]=$((RPCPORT + i))
        ENODES[i]="\"enode://${NODE_KEY}@127.0.0.1:${PP_PORTS[i]}\""
done
 
# comma separated enode allowlist
ALLOWLIST=$(IFS=,; echo "${ENODES[*]}")
 
#Create permissions configuration file (first two accounts from above genesis file)
cat > $NETWORK/permissions_config.toml << PERM_CONFIG
accounts-allowlist=["0xfe3b557e8fb62b89f4916b721be55ceb828dbd73", "0x627306090abaB3A6e1400e9345bC60c78a8BEf57"]
nodes-allowlist=[$ALLOWLIST]
PERM_CONFIG
printf "$NETWORK/permissions_config.toml\n"
cat $NETWORK/permissions_config.toml
 
#Copy key files and print node launch commands
 for((i=1;i<=${#array[@]};i++))
do
    cp "${array[i-1]}/key.pub" "${NETWORK}/Node-${i}/data/key.pub" 
    cp "${array[i-1]}/key.priv" "${NETWORK}/Node-${i}/data/key"
    cp "${NETWORK}/permissions_config.toml" "${NETWORK}/Node-${i}/data/"
 
    printf " *** Node ${i} Command *** \n"
    printf "${BESU_PATH}/bin/besu --data-path=${NETWORK}/Node-${i}/data --genesis-file=${NETWORK}/genesis.json $BOOTNODE --p2p-port=${PP_PORTS[i-1]} --rpc-http-port=${RPC_PORTS[i-1]} --permissions-nodes-config-file-enabled --permissions-accounts-config-file-enabled --rpc-http-enabled --rpc-http-api=ADMIN,ETH,NET,PERM,IBFT --host-allowlist=\"*\" --rpc-http-cors-origins=\"all\"\n" 
 
    BOOTNODE="--bootnodes=${ENODES[0]}"
done

echo -ne "==> Setup complete..."
