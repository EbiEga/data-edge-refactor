# Data-edge-refactor

EGA is one of the principle Archive resources that distributes data to the researchers. Our data distribution is made through HTTP, ASPERA and API REST HTTP. During the last couple of years we’ve seen an increase in the amount of data that the archive transfers to researchers in excess of 2PiB per year.

To cope with the increase of traffic in our Data APi we want to improve the transfer performance of our HTTP API and at the same time, upgrade the existing technology stack. We see this as a starting point for a new design that will encompass all the metadata and file transfer services.

# Proposal

## What are we going to re-do?
New data-edge: that it will be communicating with file database service and the key database service, the AAI Service and needs to be talking with FIRE. We are gonna use the latest version of the spring framework library and we will experiment with the reactive approach that could provide better performance.
Make sure that the client goes directly to the Data-Edge, and we are simplifying the traffic, the data will travel from FIRE to new-Data-Edge and to new-data-Edge to Client (finally user). 

## Advantages
We decided this proposal because we will be able to test it without any current dependencies. We will also be able to test cut-edge technology and try things that we can not try in our day by day work.

This proposal is factible to make in two people in 3 days because the code for doing that, we have it, we are going to use the API to access FIRE with the APIS3. 


# Goal

Clean up Data-API. Simplify as much as possible focusing our attention in the new Data-Edge. This will also generate future work for the EGA such the creation of the new RES, the modification of Htsget in order to use the new RES, and several tests and trials before replacing the whole thing into production. 

We will be coding a new DataEdge version from scratch that will include the re-encryption code and it will not use a third service that suppose an extra-network cost. We will aim to have a more stable service and reduce the dependencies that currently are damaging our performance.

For the first stage in this Hackathon we will focus on re-do the data-Edge, if we see that we can cover more ground during this Hackathon we will consider further modifications to clean up and reduce the service interdependencies. 
