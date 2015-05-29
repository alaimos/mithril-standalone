## Run MITHRIL and compute p-value adjustment
## in.mithril parameter is mithril input file
## out.mithril.raw parameter is mithril temporary output file
## The result is a data.frame with mithril results sorted by adjuster p-value
run.mithril <- function (in.mithril, out.mithril.raw) {
  
  command <- paste0("java -jar Mithril.jar ",
                    "-in ", in.mithril," -out ", out.mithril.raw, " -method enriched")
  
  system(command, intern=TRUE)
  
  result.mithril <- read.csv(out.mithril.raw, sep=";", header=TRUE)
  
  fdr <- p.adjust(result.mithril$P.Value, method="fdr")
  
  result.mithril <- data.frame(result.mithril, "P.Value.FDR"=fdr)
  
  rownames(result.mithril) <- as.vector(result.mithril$Pathway.ID)
  
  result.mithril <- result.mithril[order(result.mithril[,"P.Value.FDR"]), ]
  
  return (result.mithril)
}

## Write MITHRIL input file
## all.names parameter is a vector of gene/mirna names
## diff.exp.names parameter is a vector of differentially expressed gene/mirna names
## diff.exp.lfcs  parameter is a vector containing Log-Fold-Changes of differentially expressed genes/mirna
## fname parameter is the name of the file that this procedure will write
write.mithril <- function (all.names, diff.exp.names, diff.exp.lfcs, fname) {
  ff <- file(fname, "w+")
  cat("#-G", fill=TRUE, file=ff)
  cat(all.names, sep="\n", file=ff)
  cat("#-D", fill=TRUE, file=ff)
  cat(paste(diff.exp.names, diff.exp.lfcs, sep=";"), sep="\n", file=ff)
  cat("#-R", fill=TRUE, file=ff)
  cat(diff.exp.names, sep="\n", file=ff)
  close(ff)
}