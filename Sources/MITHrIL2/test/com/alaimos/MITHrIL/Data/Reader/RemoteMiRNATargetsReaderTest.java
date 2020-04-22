package com.alaimos.MITHrIL.Data.Reader;

import com.alaimos.MITHrIL.Algorithm.RepositoryEnricher;
import com.alaimos.MITHrIL.Data.Pathway.Interface.Enrichment.RepositoryEnrichmentInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import com.alaimos.MITHrIL.Data.Records.MiRNAsContainer;
import com.alaimos.MITHrIL.Data.Records.Type.EvidenceType;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.0.0.0
 * @since 24/12/2015
 */
public class RemoteMiRNATargetsReaderTest {

    @Test
    public void testReadMiRNAs() throws Exception {
        RemoteMiRNATargetsReader rm =
                new RemoteMiRNATargetsReader("https://alpha.dmi.unict.it/mithril/data/mirnas/hsa.txt.gz");
        MiRNAsContainer m = rm.readMiRNAs();
        assertNotNull(m);
        assertTrue(m.size() > 0);
        assertTrue(m.values().stream().mapToInt(mm -> mm.getTargets().size()).sum() > 0);
    }

    @Test
    public void testReadTranscriptionFactors() throws Exception {
        RemoteMiRNATargetsReader rm =
                new RemoteMiRNATargetsReader("https://alpha.dmi.unict.it/mithril/data/mirnas/hsa.txt.gz");
        RemoteMiRNATFReader tf = new RemoteMiRNATFReader("https://alpha.dmi.unict.it/mithril/data/tf/hsa.txt.gz");
        MiRNAsContainer m = rm.readMiRNAs();
        tf.readTranscriptionFactors(m);
        assertNotNull(m);
        assertTrue(m.size() > 0);
        assertTrue(m.values().stream().mapToInt(mm -> mm.getTranscriptionFactors().size()).sum() > 0);
    }

    @Test
    public void testPathwayEnrichment() throws Exception {
        RemoteMiRNATargetsReader rm =
                new RemoteMiRNATargetsReader("https://alpha.dmi.unict.it/mithril/data/mirnas/hsa.txt.gz");
        RemoteMiRNATFReader tf = new RemoteMiRNATFReader("https://alpha.dmi.unict.it/mithril/data/tf/hsa.txt.gz");
        MiRNAsContainer m = rm.readMiRNAs();
        tf.readTranscriptionFactors(m);
        RemotePathwayRepositoryReader rp =
                new RemotePathwayRepositoryReader("https://alpha.dmi.unict.it/mithril/data/pathways/hsa.txt.gz");
        RepositoryInterface r = rp.read();
        RepositoryEnrichmentInterface re = m.toEnrichment(EvidenceType.STRONG);
        assertNotNull(re);
        assertTrue(r.size() > 0);
        RepositoryEnricher repoEnricher = new RepositoryEnricher();
        HashMap<String, Object> tmp = new HashMap<>();
        tmp.put("repository", r);
        tmp.put("enrichment", re);
        repoEnricher.init().setParameters(tmp).run();
        RepositoryInterface er = repoEnricher.getOutput();
        assertNotNull(er);
        assertTrue(er.size() > 0);
    }
}