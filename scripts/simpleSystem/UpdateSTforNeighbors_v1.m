function St = UpdateSTforNeighbors_v1(St,n1,n2,N1,N2,val)
%
%Modify the Site Type value for the six neighbors of site (n1,n2).
%Modified value = Previous value + val, where val = +1 or -1
%

if ( val ~= 1 && val ~= -1 ), fprintf(1,'Wrong value for argument VAL! Aborting... '), pause, end

n1e=n1+1; if (n1e > N1), n1e = 1; end
n1w=n1-1; if (n1w < 1), n1w = N1; end
n2n=n2+1; if (n2n > N2), n2n = 1; end
n2s=n2-1; if (n2s < 1), n2s = N2; end

St(n1e,n2 )=St(n1e,n2 )+val;
St(n1 ,n2n)=St(n1 ,n2n)+val;
St(n1w,n2n)=St(n1w,n2n)+val;
St(n1w,n2 )=St(n1w,n2 )+val;
St(n1 ,n2s)=St(n1 ,n2s)+val;
St(n1e,n2s)=St(n1e,n2s)+val;

return
