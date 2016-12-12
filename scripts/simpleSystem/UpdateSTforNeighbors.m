function [St,numST,NMA,NFA] = UpdateSTforNeighbors(St,So,n1,n2,N1,N2,val,numST,NMA,NFA)
%function [St,Nt,numST,NMA,NFA,NNI] = UpdateSTforNeighbors(St,So,Nt,n1p,n2p,N1,N2, 1,numST,NMA,NFA,NNI);            
%
% Modify the Site Type value for the six neighbors of site (n1,n2).
% Modified value = Previous value + val, where val = +1 or -1
%

if ( val ~= 1 && val ~= -1 ), fprintf(1,'Wrong value for argument VAL! Aborting... '), pause, end

n1e=n1+1; if (n1e > N1), n1e = 1; end
n1w=n1-1; if (n1w < 1), n1w = N1; end
n2n=n2+1; if (n2n > N2), n2n = 1; end
n2s=n2-1; if (n2s < 1), n2s = N2; end

St(n1e,n2 )=min( St(n1e,n2 )+val , 6);
St(n1 ,n2n)=min( St(n1 ,n2n)+val , 6);
St(n1w,n2n)=min( St(n1w,n2n)+val , 6);
St(n1w,n2 )=min( St(n1w,n2 )+val , 6);
St(n1 ,n2s)=min( St(n1 ,n2s)+val , 6);
St(n1e,n2s)=min( St(n1e,n2s)+val , 6);

St(n1e,n2) = max(St(n1e,n2),0);
St(n1 ,n2n) = max(St(n1 ,n2n),0);
St(n1w,n2n) = max(St(n1w,n2n),0);
St(n1w,n2) = max(St(n1w,n2),0);
St(n1 ,n2s) = max(St(n1 ,n2s),0);
St(n1e,n2s) = max(St(n1e,n2s),0);

if So(n1e,n2 ) == 1, [numST,NMA,NFA]=Update_numST(n1e,n2 ,numST,val,St); end
if So(n1 ,n2n) == 1, [numST,NMA,NFA]=Update_numST(n1 ,n2n,numST,val,St); end
if So(n1w,n2n) == 1, [numST,NMA,NFA]=Update_numST(n1w,n2n,numST,val,St); end
if So(n1w,n2 ) == 1, [numST,NMA,NFA]=Update_numST(n1w,n2 ,numST,val,St); end
if So(n1 ,n2s) == 1, [numST,NMA,NFA]=Update_numST(n1 ,n2s,numST,val,St); end
if So(n1e,n2s) == 1, [numST,NMA,NFA]=Update_numST(n1e,n2s,numST,val,St); end

return

%If central atom has become frozen (4 or more neighbors), update the
%Nucleus Tag (Nt) and the Number of Nucleated Islands:
if ( St(n1p,n2p) == 4 )
    minNt = 1e6;
    if Nt(n1e,n2 ) > 0, minNt = min(minNt,Nt(n1e,n2 )); end
    if Nt(n1 ,n2n) > 0, minNt = min(minNt,Nt(n1e,n2 )); end
    if Nt(n1w,n2n) > 0, minNt = min(minNt,Nt(n1e,n2 )); end
    if Nt(n1w,n2 ) > 0, minNt = min(minNt,Nt(n1e,n2 )); end
    if Nt(n1 ,n2s) > 0, minNt = min(minNt,Nt(n1e,n2 )); end
    if Nt(n1e,n2s) > 0, minNt = min(minNt,Nt(n1e,n2 )); end
    if minNt == 1e6;
        NNI = NNI + 1;
        Nt(n1p,n2p) = NNI;
        if So(n1e,n2 ) == 1, Nt(n1e,n2 ) = NNI; end
        if So(n1 ,n2n) == 1, Nt(n1 ,n2n) = NNI; end
        if So(n1w,n2n) == 1, Nt(n1w,n2n) = NNI; end
        if So(n1w,n2 ) == 1, Nt(n1w,n2 ) = NNI; end
        if So(n1 ,n2s) == 1, Nt(n1 ,n2s) = NNI; end
        if So(n1e,n2s) == 1, Nt(n1e,n2s) = NNI; end
    else
        Nt(n1p,n2p) = minNt;
    end
end











% St(n1e,n2 )=St(n1e,n2 )+val;
% if ( St(n1e,n2 ) > 6 ), fprintf(1,'Error: St(n1e,n2 ) > 6 \n'), pause, end
% [numST,NMA,NFA]=Update_numST(n1e,n2 ,numST,val,St);
% St(n1 ,n2n)=St(n1 ,n2n)+val;
% if ( St(n1 ,n2n) > 6 ), fprintf(1,'Error: St(n1 ,n2n) > 6 \n'), pause, end
% [numST,NMA,NFA]=Update_numST(n1 ,n2n,numST,val,St);
% St(n1w,n2n)=St(n1w,n2n)+val;
% if ( St(n1w,n2n) > 6 ), fprintf(1,'Error: St(n1w,n2n) > 6 \n'), pause, end
% [numST,NMA,NFA]=Update_numST(n1w,n2n,numST,val,St);
% St(n1w,n2 )=St(n1w,n2 )+val;
% if ( St(n1w,n2 ) > 6 ), fprintf(1,'Error: St(n1w,n2 ) > 6 \n'), pause, end
% [numST,NMA,NFA]=Update_numST(n1w,n2 ,numST,val,St);
% St(n1 ,n2s)=St(n1 ,n2s)+val;
% if ( St(n1 ,n2s) > 6 ), fprintf(1,'Error: St(n1 ,n2s) > 6 \n'), pause, end
% [numST,NMA,NFA]=Update_numST(n1 ,n2s,numST,val,St);
% St(n1e,n2s)=St(n1e,n2s)+val;
% if ( St(n1e,n2s) > 6 ), fprintf(1,'Error: St(n1e,n2s) > 6 \n'), pause, end
% [numST,NMA,NFA]=Update_numST(n1e,n2s,numST,val,St);

return
