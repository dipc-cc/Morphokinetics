function [n1p,n2p]=DetermineFinalSite(n1,n2,N1,N2,proc)
if ( proc < 1 || proc > 8 ), fprintf(1,'Error: proc = %8i \n',proc), pause, end

%Determine location of final site:
n1e=n1+1; if (n1e > N1), n1e = 1; end
n1w=n1-1; if (n1w < 1), n1w = N1; end
n2n=n2+1; if (n2n > N2), n2n = 1; end
n2s=n2-1; if (n2s < 1), n2s = N2; end
if proc==1 %Hop to the 1st neighbor
    n1p=n1e; n2p=n2;
elseif proc==2 %Hop to the 2nd neighbor
    n1p=n1; n2p=n2n;
elseif proc==3 %Hop to the 2nd neighbor
    n1p=n1w; n2p=n2n;
elseif proc==4 %Hop to the 2nd neighbor
    n1p=n1w; n2p=n2;
elseif proc==5 %Hop to the 2nd neighbor
    n1p=n1; n2p=n2s;
elseif proc==6 %Hop to the 2nd neighbor
    n1p=n1e; n2p=n2s;
end

return
