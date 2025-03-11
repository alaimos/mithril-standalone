# MITHrIL 2: Mirna enrIched paTHway Impact anaLysis

This repository hosts the second release of MITHrIL algorithm

The second releases of MITHrIL also includes support for multiple species, meta-pathway approach, and an implementation of the SPECIFIC algorithm.


#### If you are using this software, please cite:

- Alaimo, S., Giugno, R., Acunzo, M., Veneziano, D., Ferro, A. and Pulvirenti, A., 2016. **Post-transcriptional knowledge in pathway analysis increases the accuracy of phenotypes classification**. Oncotarget, 7(34), p.54572.
- Alaimo, S., Marceca, G.P., Ferro, A. and Pulvirenti, A., 2017. **Detecting disease specific pathway substructures through an integrated systems biology approach**. Non-coding RNA, 3(2), p.20.

### Supported species

| Id           | Name                                                     | Has miRNA-targets interactions?| Has TF-miRNAs activations?|
|--------------|----------------------------------------------------------|--------------------------------|---------------------------|
| gga          | Gallus gallus (chicken)                                  | Yes                            | Yes                       |
| acb          | Acinetobacter baumannii ATCC 17978                       | No                             | No                        |
| ssc          | Sus scrofa (pig)                                         | Yes                            | Yes                       |
| ecb          | Equus caballus (horse)                                   | No                             | No                        |
| hsa2022      | Homo sapiens version 2022 (human)                        | Yes                            | Yes                       |
| mcc          | Macaca mulatta (rhesus monkey)                           | No                             | No                        |
| ssc_v2023_03 | Sus scrofa (pig) version 2023_03                         | Yes                            | No                        |
| pau_v2023_03 | Pseudomonas aeruginosa UCBPP-PA14 version 2023_03        | No                             | No                        |
| ath          | Arabidopsis thaliana (thale cress)                       | Yes                            | Yes                       |
| bna          | Brassica napus (rape)                                    | No                             | No                        |
| dre          | Danio rerio (zebrafish)                                  | Yes                            | Yes                       |
| mcc_v2023_03 | Macaca mulatta (rhesus monkey) version 2023_03           | No                             | No                        |
| brp          | Brassica rapa (field mustard)                            | No                             | No                        |
| bta_v2023_03 | Bos taurus (cow) version 2023_03                         | Yes                            | No                        |
| ola          | Oryzias latipes (Japanese medaka)                        | No                             | No                        |
| pau          | Pseudomonas aeruginosa UCBPP-PA14                        | No                             | No                        |
| hsa2018      | Homo sapiens version 2018 (human)                        | Yes                            | Yes                       |
| vvi          | Vitis vinifera (wine grape)                              | No                             | No                        |
| dre_v2023_03 | Danio rerio (zebrafish) version 2023_03                  | Yes                            | Yes                       |
| pfa          | Plasmodium falciparum 3D7                                | No                             | No                        |
| acb_v2023_03 | Acinetobacter baumannii ATCC 17978 version 2023_03       | No                             | No                        |
| hsa2015      | Homo sapiens version 2015 (human)                        | Yes                            | Yes                       |
| bmor_v2023_03| Bombyx mori (domestic silkworm) version 2023_03          | Yes                            | No                        |
| brp_v2023_03 | Brassica rapa (field mustard) version 2023_03            | No                             | No                        |
| cel_v2023_03 | Caenorhabditis elegans (nematode) version 2023_03        | Yes                            | Yes                       |
| gga_v2023_03 | Gallus gallus (chicken) version 2023_03                  | Yes                            | Yes                       |
| rno          | Rattus norvegicus (rat)                                  | Yes                            | Yes                       |
| xtr_v2023_03 | Xenopus tropicalis (tropical clawed frog) version 2023_03| Yes                            | No                        |
| pfa_v2023_03 | Plasmodium falciparum 3D7 version 2023_03                | No                             | No                        |
| tgu_v2023_03 | Taeniopygia guttata (zebra finch) version 2023_03        | Yes                            | No                        |
| vvi_v2023_03 | Vitis vinifera (wine grape) version 2023_03              | No                             | No                        |
| hsa          | Homo sapiens (human)                                     | Yes                            | Yes                       |
| rno_v2023_03 | Rattus norvegicus (rat) version 2023_03                  | Yes                            | Yes                       |
| boe          | Brassica oleracea (wild cabbage)                         | No                             | No                        |
| boe_v2023_03 | Brassica oleracea (wild cabbage) version 2023_03         | No                             | No                        |
| oas_v2023_03 | Ovis aries (sheep) version 2023_03                       | Yes                            | No                        |
| bmor         | Bombyx mori (domestic silkworm)                          | Yes                            | No                        |
| cfa_v2023_03 | Canis lupus familiaris (dog) version 2023_03             | Yes                            | No                        |
| sly          | Solanum lycopersicum (tomato)                            | Yes                            | Yes                       |
| sly_v2023_03 | Solanum lycopersicum (tomato) version 2023_03            | Yes                            | No                        |
| ptr_v2023_03 | Pan troglodytes (chimpanzee) version 2023_03             | Yes                            | No                        |
| mmu_v2023_03 | Mus musculus (house mouse) version 2023_03               | Yes                            | Yes                       |
| cel          | Caenorhabditis elegans (nematode)                        | Yes                            | Yes                       |
| bta          | Bos taurus (cow)                                         | Yes                            | No                        |
| cal          | Candida albicans                                         | No                             | No                        |
| hsa_v2023_03 | Homo sapiens (human) version 2023_03                     | Yes                            | Yes                       |
| mmu          | Mus musculus (house mouse)                               | Yes                            | Yes                       |
| dme_v2023_03 | Drosophila melanogaster (fruit fly) version 2023_03      | Yes                            | Yes                       |
| ath_v2023_03 | Arabidopsis thaliana (thale cress) version 2023_03       | Yes                            | Yes                       |
| cfa          | Canis lupus familiaris (dog)                             | Yes                            | No                        |
| oas          | Ovis aries (sheep)                                       | Yes                            | No                        |
| xla_v2023_03 | Xenopus laevis (African clawed frog) version 2023_03     | Yes                            | No                        |
| tgu          | Taeniopygia guttata (zebra finch)                        | Yes                            | No                        |
| ecb_v2023_03 | Equus caballus (horse) version 2023_03                   | Yes                            | No                        |
| xla          | Xenopus laevis (African clawed frog)                     | No                             | No                        |
| ptr          | Pan troglodytes (chimpanzee)                             | Yes                            | No                        |
| pps          | Pan paniscus (bonobo)                                    | No                             | No                        |
| bna_v2023_03 | Brassica napus (rape) version 2023_03                    | No                             | No                        |
| pps_v2023_03 | Pan paniscus (bonobo) version 2023_03                    | Yes                            | No                        |
| ola_v2023_03 | Oryzias latipes (Japanese medaka) version 2023_03        | Yes                            | No                        |
| xtr          | Xenopus tropicalis (tropical clawed frog)                | Yes                            | No                        |
| dme          | Drosophila melanogaster (fruit fly)                      | Yes                            | Yes                       |


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

