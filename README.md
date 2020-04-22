# MITHrIL 2: Mirna enrIched paTHway Impact anaLysis

This repository hosts the second release of MITHrIL algorithm

The second releases of MITHrIL also includes support for multiple species, meta-pathway approach, and an implementation of the SPECIFIC algorithm.


#### If you are using this software, please cite:

- Alaimo, S., Giugno, R., Acunzo, M., Veneziano, D., Ferro, A. and Pulvirenti, A., 2016. **Post-transcriptional knowledge in pathway analysis increases the accuracy of phenotypes classification**. Oncotarget, 7(34), p.54572.
- Alaimo, S., Marceca, G.P., Ferro, A. and Pulvirenti, A., 2017. **Detecting disease specific pathway substructures through an integrated systems biology approach**. Non-coding RNA, 3(2), p.20.

### Supported species

| Id     | Name                                    | Has miRNA-targets interactions?| Has TF-miRNAs activations?|
|--------|-----------------------------------------|--------------------------------|---------------------------|
| hsa    | Homo sapiens latest version (human)     | Yes                            | Yes                       |
| hsa2015| Homo sapiens version 2015 (human)       | Yes                            | Yes                       |
| hsa2018| Homo sapiens version 2018 (human)       | Yes                            | Yes                       |
| mmu    | Mus musculus (mouse)                    | Yes                            | Yes                       |
| rno    | Rattus norvegicus (rat)                 | Yes                            | Yes                       |
| bmor   | Bombyx mori (domestic silkworm)         | Yes                            | No                        |
| gga    | Gallus gallus (chicken)                 | Yes                            | Yes                       |
| sly    | Solanum lycopersicum (tomato)           | No                             | No                        |
| ssc    | Sus scrofa (pig)                        | Yes                            | No                        |
| ecb    | Equus caballus (horse)                  | No                             | No                        |
| cel    | Caenorhabditis elegans (nematode)       | Yes                            | Yes                       |
| mcc    | Macaca mulatta (rhesus monkey)          | No                             | No                        |
| bta    | Bos taurus (cow)                        | Yes                            | No                        |
| ath    | Arabidopsis thaliana (thale cress)      | Yes                            | Yes                       |
| dre    | Danio rerio (zebrafish)                 | Yes                            | Yes                       |
| cfa    | Canis familiaris (dog)                  | Yes                            | No                        |
| oas    | Ovis aries (sheep)                      | Yes                            | No                        |
| tgu    | Taeniopygia guttata (zebra finch)       | Yes                            | No                        |
| ola    | Oryzias latipes (Japanese medaka)       | Yes                            | No                        |
| pau    | Pseudomonas aeruginosa UCBPP-PA14       | No                             | No                        |
| vvi    | Vitis vinifera (wine grape)             | No                             | No                        |
| pfa    | Plasmodium falciparum 3D7               | No                             | No                        |
| xla    | Xenopus laevis (African clawed frog)    | Yes                            | No                        |
| ptr    | Pan troglodytes (chimpanzee)            | No                             | No                        |
| pps    | Pan paniscus (bonobo)                   | No                             | No                        |
| xtr    | Xenopus tropicalis (western clawed frog)| Yes                            | No                        |
| dme    | Drosophila melanogaster (fruit fly)     | Yes                            | Yes                       |

### Available services

MITHrIL 2 includes several services. By running the jar application, a list of available services is displayed:

```bash
java -jar MITHrIL2.jar
```

To run a service you just use its name:

```bash
java -jar MITHrIL2.jar <service_name>
```

A list of the major services is displayed in the following table:

   | Name              | Description                                                                                 |
   |-------------------|---------------------------------------------------------------------------------------------|
   | batch-mithril     | runs MITHrIL 2 algorithm on a batch of log-fold-changes                                     |
   | convert           | conversion between MITHrIL 2 and MITHrIL 1                                                  |
   | exportgraph       | export pathway graph.                                                                       |
   | exportstructs     | runs SPECIFIC algorithm: exports all specific substructures found in a MITHrIL 2 experiment.|
   | mithril           | runs MITHrIL 2 algorithm on a sample (use -b option to build SPECIFIC input file)           |
   | organisms         | Lists all organisms and their characteristics                                               |
   | pathway-categories| shows all pathway categories for a species                                                  |
   | version           | Shows current version of MITHrIL and checks for updates                                     |

