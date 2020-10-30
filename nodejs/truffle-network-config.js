besu: {
   network_id: "*",
   gas: 0,
   gasPrice: 0,
   provider: new HDWalletProvider(
     fs.readFileSync("<path to a file with a mnemonic>", "utf-8"),
     "<besu jsonrpc endpoint>"
   ),
 },
