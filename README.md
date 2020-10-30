---
title: EVM Tool Reference
version: 2020-10
description: ethereum virtual machine command line tool 
---

# EVM Tool Reference

Options for running:

* [Arbitrary EVM programs](#run-options)
* [Ethereum State Tests](#state-test-options).

## Quick Overview

```bash
$ ethereum/evmtool/build/install/evmtool/bin/evm --help
Usage:

evm [OPTIONS] [COMMAND]

Description:

This command evaluates EVM transactions.

Options:
      --code=<code>          Byte stream of code to be executed.
      --gas=<int>            Amount of gas for this invocation.
      --price=<int>          Price of gas (in GWei) for this invocation
      --sender=<address>     Calling address for this invocation.
      --receiver=<address>   Receiving address for this invocation.
      --input=<code>         The CALLDATA for this invocation
      --value=<int>          The amount of ether attached to this invocation
      --json                 Trace each opcode as a json object.
      --nomemory             Disable showing the full memory output for each op.
      --genesis, --prestate=<genesisFile>
                             The genesis file containing account data for this
                               invocation.
      --chain=<network>      Name of a well known network that will be used for
                               this invocation.
      --repeat=<repeat>      Number of times to repeat for benchmarking.
  -h, --help                 Show this help message and exit.
  -V, --version              Print version information and exit.
      --revert-reason-enabled[=<Boolean>]
                             Should revert reasons be persisted. (default: true)
      --key-value-storage=<keyValueStorageName>
                             Identity for the key-value storage to be used
                               (default: 'memory' alternate: 'rocksdb')
      --data-path=<PATH>     If using RocksDB storage, the path to Besu data
                               directory (default:
                               /Users/sbacha/besu-tool/build/data)
      --block-number=<blockParameter>
                             Block number to evaluate against (default:
                               'PENDING', or 'EARLIEST', 'LATEST', or a number)
Commands:
  state-test  Execute an Ethereum State Test.

Hyperledger Besu is licensed under the Apache License 2.0
```

## Run Options

The first mode of the EVM tool runs an arbitrary EVM and is invoked without an extra command. Command Line
Options specify the code and other contextual information.

- [EVM Tool Reference](#evm-tool-reference)
  * [Run Options](#run-options)
    + [code](#code)
        * ["Syntax"](#-syntax-)
        * ["Example"](#-example-)
    + [gas](#gas)
        * ["Syntax"](#-syntax--1)
        * ["Example"](#-example--1)
    + [price](#price)
        * ["Syntax"](#-syntax--2)
        * ["Example"](#-example--2)
    + [sender](#sender)
        * ["Syntax"](#-syntax--3)
        * ["Example"](#-example--3)
    + [receiver](#receiver)
        * ["Syntax"](#-syntax--4)
        * ["Example"](#-example--4)
    + [input](#input)
        * ["Syntax"](#-syntax--5)
        * ["Example"](#-example--5)
    + [value](#value)
        * ["Syntax"](#-syntax--6)
        * ["Example"](#-example--6)
    + [json](#json)
        * ["Syntax"](#-syntax--7)
        * ["Example"](#-example--7)
    + [nomemory](#nomemory)
        * ["Syntax"](#-syntax--8)
        * ["Example"](#-example--8)
    + [genesis](#genesis)
        * ["Syntax"](#-syntax--9)
        * ["Example"](#-example--9)
    + [chain](#chain)
        * ["Syntax"](#-syntax--10)
        * ["Example"](#-example--10)
    + [repeat](#repeat)
        * ["Syntax"](#-syntax--11)
        * ["Example"](#-example--11)
    + [revert-reason-enabled](#revert-reason-enabled)
        * ["Syntax"](#-syntax--12)
        * ["Example"](#-example--12)
    + [key-value-storage](#key-value-storage)
        * ["Syntax"](#-syntax--13)
        * ["Example"](#-example--13)
    + [data-path](#data-path)
        * ["Syntax"](#-syntax--14)
        * ["Example"](#-example--14)
    + [block-number](#block-number)
        * ["Syntax"](#-syntax--15)
        * ["Example"](#-example--15)
  * [State Test Options](#state-test-options)
    + [Applicable Options](#applicable-options)
      - [json](#json-1)
        * ["Syntax"](#-syntax--16)
        * ["Example"](#-example--16)
    + [Using command arguments](#using-command-arguments)
        * ["Docker Example"](#-docker-example-)
        * ["CLI Example"](#-cli-example-)
    + [Using Standard Input](#using-standard-input)
        * ["Docker Example"](#-docker-example--1)
        * ["CLI Example"](#-cli-example--1)


### code

##### "Syntax"

```bash
--code=<code as hex string>
```

##### "Example"

```bash
--code=5B600080808060045AFA50600056
```

The code to be executed, in compiled hex code form.  There is no default value and execution fails if
this is not set.

### gas

##### "Syntax"

```bash
--gas=<gas as a decimal integer>
```

##### "Example"

```bash
--gas=100000000
```

Amount of gas to make available to the EVM.  The default value is 10 Billion, an incredibly large number
unlikely to be seen in any production blockchain.

### price

##### "Syntax"

```bash
--price=<gas price in GWei as a decimal integer>
```

##### "Example"

```bash
--price=10
```

Price of gas in GWei. The default is zero.  If set to a non-zero value, the sender account must have
enough value to cover the gas fees.

### sender

##### "Syntax"

```bash
--sender=<address>
```

##### "Example"

```bash
--sender=0xfe3b557e8fb62b89f4916b721be55ceb828dbd73
```

The account the invocation is sent from.  The specified account must exist in the world state, which
unless specified by `--genesis` or `--prestate` is the set of [accounts used for testing](Accounts-for-Testing.md).

### receiver

##### "Syntax"

```bash
--receiver=<address>
```

##### "Example"

```bash
--receiver=0x588108d3eab34e94484d7cda5a1d31804ca96fe7
```

The account the invocation is sent to.  The specified account does not need to exist.

### input

##### "Syntax"

```bash
--input=<hex binary>
```

##### "Example"

```bash
--input=9064129300000000000000000000000000000000000000000000000000000000
```

The data passed into the call.  Corresponds to the `data` field of the transaction and is returned by
the `CALLDATA` and related opcodes.

### value

##### "Syntax"

```bash
--value=<Wei in decimal>
```

##### "Example"

```bash
--value=1000000000000000000
```

The value of Ether attached to this transaction.  For operations that query the value or transfer it
to other accounts this is the amount that is available.  The amount is not reduced to cover intrinsic
cost and gas fees.

### json

##### "Syntax"

```bash
--json=<boolean>
```

##### "Example"

```bash
--json=true
```

Provide an operation-by-operation trace of the command in json when set to true.

### nomemory

##### "Syntax"

```bash
--nomemory=<boolean>
```

##### "Example"

```bash
--nomemory=true
```

By default, when tracing operations the memory is traced for each operation.  For memory heavy scripts,
setting this option may reduce the volume of json output.

### genesis

##### "Syntax"

```bash
--genesis=<path>
```

##### "Example"

```bash
--genesis=/opt/besu/genesis.json
```

The Besu Genesis file to use when evaluating the EVM.  Most useful are the `alloc` items that set up
accounts and their stored memory states.  For a complete description of this file see [Genesis File Items](Config-Items.md).

`--prestate` is a deprecated alternative option name.

### chain

##### "Syntax"

```bash
--chain=<mainnet|ropsten|rinkeby|goerli|classic|mordor|kotti|dev>
```

##### "Example"

```bash
--chain=goerli
```

The well-known network genesis file to use when evaluating the EVM.  These values are an alternative
to the `--genesis` option for well known networks.

### repeat

##### "Syntax"

```bash
--repeat=<integer>
```

##### "Example"

```bash
--repeat=1000
```

Number of times to repeat the contract before gathering timing information.  This is useful when benchmarking
EVM operations.

### revert-reason-enabled

##### "Syntax"

```bash
--revert-reason-enabled=<boolean>
```

##### "Example"

```bash
--revert-reason-enabled=true
```

If enabled, the json tracing includes the reason included in `REVERT` operations.

### key-value-storage

##### "Syntax"

```bash
--key-value-storage=<memory|rocksdb>
```

##### "Example"

```bash
--key-value-storage=rocksdb
```

Kind of key value storage to use.

Occasionally it may be useful to execute isolated EVM calls in context of an actual world state.
The default is `memory`, which executes the call only in context of the world provided by `--genesis`
or `--network` at block zero.  When set to `rocksdb` and combined with `--data-path`, `--block-number`,
and `--genesis` a Besu node that is not currently running can be used to provide the appropriate world state
for a transaction.  Useful when evaluating consensus failures.

### data-path

##### "Syntax"

```bash
--data-path=<path>
```

##### "Example"

```bash
--data-path=/opt/besu/data
```

When using `rocksdb` for `key-value-storage`, specifies the location of the database on disk.

### block-number

##### "Syntax"

```bash
--block-number=<integer>
```

##### "Example"

```bash
--block-number=10000000
```

The block number to evaluate the code against.  Used to ensure that the EVM is evaluating the
code against the correct fork, or to specify the specific world state when running with `rocksdb` for `key-value-storage`.

## State Test Options

The `state-test` sub command allows the Ethereum State Tests to be evaluated.  Most of the options
from EVM execution do not apply.

### Applicable Options

#### json

##### "Syntax"

```bash
--json=<boolean>
```

##### "Example"

```bash
--json=true
```

Provide an operation by operation trace of the command in json when set to true.  Set to true for EVMLab
Fuzzing.  Whether or not `json` is set, a summary JSON object is printed to standard output for each
state test executed.

### Using command arguments

If you use command arguments, you can list one or more state tests.  All of the state tests are evaluated
in the order they are specified.

##### "Docker Example"

```bash
docker run --rm -v ${PWD}:/opt/referencetests hyperledger/besu-evmtool:develop --json state-test /opt/referencetests/GeneralStateTests/stExample/add11.json
```

##### "CLI Example"

```bash
evm --json state-test stExample/add11.json
```

### Using Standard Input

If no reference tests are passed in using the command line, the EVM Tool loads one complete json object
from standard input and executes that state test.

##### "Docker Example"

```bash
docker run --rm  -i hyperledger/besu-evmtool:develop --json state-test < stExample/add11.json
```

##### "CLI Example"

```bash
evm --json state-test < stExample/add11.json
```
