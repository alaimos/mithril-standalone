package com.alaimos.PHENSIM.Data.Reader;

import com.alaimos.Commons.Reader.AbstractRemoteDataReader;
import com.alaimos.Commons.Reader.RemoteDataReaderInterface;
import com.alaimos.MITHrIL.Data.Pathway.Factory.PathwayFactory;
import com.alaimos.MITHrIL.Data.Pathway.Interface.NodeInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayFactoryInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.PathwayInterface;
import com.alaimos.MITHrIL.Data.Pathway.Interface.RepositoryInterface;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

/**
 * @author Salvatore Alaimo, Ph.D.
 * @version 2.2.0.1
 */
public class RemoteReactomeRepositoryReader extends AbstractRemoteDataReader<RepositoryInterface> {

    private static final String SEPARATOR = "\t";
    private static final String OTHER_SEPARATOR = ",";

    private final PathwayFactoryInterface pf = PathwayFactory.getInstance();
    private final RepositoryInterface repository;
    private final HashMap<String, NodeInterface> allNodes = new HashMap<>();

    public RemoteReactomeRepositoryReader(String url, RepositoryInterface r) {
        setPersisted(true).setUrl(url);
        repository = r;
    }

    @Override
    public RemoteDataReaderInterface<RepositoryInterface> setUrl(String url) {
        super.setUrl(url);
        setFile("pathway-repository-reactome-" + FilenameUtils.getName(url));
        return this;
    }

    private void findAllNodes() {
        for (var p : repository) {
            if (p.hasGraph()) {
                allNodes.putAll(p.getGraph().getNodes());
            }
        }
    }

    private void createPathway(@NotNull String[] line) {
        var pathwayId = line[0];
        if (!repository.containsPathway(pathwayId)) {
            PathwayInterface p = pf.getPathway(pathwayId, line[1], pf.getGraph(), "reactome");
            p.getGraph().setOwner(p);
            repository.add(p);
        }
    }

    private NodeInterface readNode(@NotNull String[] line, int field) {
        var nodeId = line[field];
        var nodeName = line[field + 1];
        if (nodeId.equalsIgnoreCase("na")) return null;
        if (allNodes.containsKey(nodeId)) {
            var node = allNodes.get(nodeId);
            node.setName(nodeName);
            return node;
        } else {
            var nodeType = (nodeId.startsWith("chebi:") || nodeId.startsWith("cpd:")) ? "compound" : "gene";
            var node = pf.getNode(nodeId, nodeName, nodeType);
            allNodes.put(nodeId, node);
            return node;
        }
    }

    private void createEdge(String[] line) {
        var start = readNode(line, 2);
        var end = readNode(line, 4);
        if (start == null || end == null) return;
        var owner = repository.getPathwayById(line[0]);
        var e = pf.getEdge(start, end, line[7], line[8], owner);
        owner.getGraph().addEdge(e);
    }

    private void readEndpoints(@NotNull String[] line) {
        var endpoints = line[9].split(OTHER_SEPARATOR, -1);
        var epList = Arrays.stream(endpoints).map(String::trim).filter(trim -> !trim.isEmpty())
                .collect(Collectors.toList());
        if (epList.size() > 0) {
            PathwayInterface p = repository.getPathwayById(line[0]);
            if (p != null && p.hasGraph()) {
                p.getGraph().setEndpoints(epList);
            }
        }
    }

    @Override
    protected RepositoryInterface realReader() {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(getInputStream()))) {
            String line, pathwayId;
            String[] sLine;
            boolean create;
            findAllNodes();
            while ((line = r.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    if (line.startsWith("pathwayId")) continue;
                    sLine = line.split(SEPARATOR, -1);
                    pathwayId = sLine[0];
                    if (sLine.length != 11 || sLine[10].equalsIgnoreCase("true"))
                        continue;
                    create = !repository.containsPathway(pathwayId);
                    if (create) createPathway(sLine);
                    createEdge(sLine);
                    if (create) readEndpoints(sLine);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
        return repository;
    }
}
