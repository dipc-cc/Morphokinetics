function [v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForNeighbors(v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1,n2,N1,N2,So,St,rlevel,g,k)
%
%
%                                      

n1e=n1+1; if (n1e > N1), n1e = 1; end
n1w=n1-1; if (n1w < 1), n1w = N1; end
n2n=n2+1; if (n2n > N2), n2n = 1; end
n2s=n2-1; if (n2s < 1), n2s = N2; end

[v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1e,n2 ,N1,N2,So,St,g,k);
[v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1 ,n2n,N1,N2,So,St,g,k);
[v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1w,n2n,N1,N2,So,St,g,k);
[v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1w,n2 ,N1,N2,So,St,g,k);
[v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1 ,n2s,N1,N2,So,St,g,k);
[v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1e,n2s,N1,N2,So,St,g,k);


return
