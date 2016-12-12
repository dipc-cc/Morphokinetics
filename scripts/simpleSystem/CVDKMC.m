%function CVDKMC
%
%KMC simulation of monlayer growth
%

%Geometry:
N1=50; %Number of sites along direction 1
N2=50; %Number of sites along direction 2
L = 1.0; %Interatomic distance

%Total Number of Events:
NE=1e6;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Hop rates:
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
v0=1.0e+11;
v1=1.0e+10;
v2=1.0e+9;
v3=1.0e+8;
v4=0.0e+0;
v5=0.0e+0;
kB = 8.6173e-5; %Boltmann constant in eV/K
T=400; %Temperature
vv0=1e+13; %Typical attempt frequency
vv=[v0 v1 v2 v3];
EE=kB*400*log(vv0./vv); %0.1587    0.2381    0.3175    0.3968 eV (T=400, vv0=1e+13)
v0=vv0*exp(-EE(1)/kB/T)
v1=vv0*exp(-EE(2)/kB/T)
v2=vv0*exp(-EE(3)/kB/T)
v3=vv0*exp(-EE(4)/kB/T)
hrt  =[v0     v0     v0     v0      0      0     0;
       v1     v1     v1     v1     v1      0     0;
       v2     v2     v2     v2     v2     v2     0;
       v3     v3     v3     v3     v3     v3     0;
        0      0      0      0     v4      0     0;
        0      0      0      0      0     v5     0;
        0      0      0      0      0      0     0];

hrt_f=[1.000   2.500  5.000  7.500  0      0     0;
       0.001   1.000  2.500  5.000  7.500  0     0;
       0.001   1.000  1.000  2.500  5.000  7.500 0;
       0       0      0.001  1.000  2.500  5.000 0;
       0       0      0      0      1.000  0     0;
       0       0      0      0      0      1.000 0;
       0       0      0      0      0      0     0];
hrt=hrt.*hrt_f;



%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Adsorption rates:
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
a0=5.0e+6; %ok: 5.0e+4 5.0e+5; 5.0e+6;    too large: 5.0e+7
art=[a0 a0/2 a0/4 a0/8 a0/16 a0/32 a0/64];


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Desorption rates:
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
d0=0;%2.5e2; %2.5e+9;
drt=[d0 d0/8 d0/64 d0/256 d0/512 d0/1024 d0/2048];


%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Initialize variables:
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%Site Occupation:
So=zeros(N1,N2);
%So(24,30)=1; 
%So(15,19)=1;

%Site Type:
%ST(n1,n2) = 0 (zero neighbors           = adatom  )
%ST(n1,n2) = 1 (one neighbor             = corner  )
%ST(n1,n2) = 2 (two neighbors            = edge    )
%ST(n1,n2) = 3 (three neighbors          = kink    )
%ST(n1,n2) = 4 (four or more neighbors   = bulk    )
St=zeros(N1,N2);
for n1=1:N1
    for n2=1:N2
        if (So(n1,n2)==1)
            St = UpdateSTforNeighbors_v1(St,n1,n2,N1,N2,1);
        end
    end
end

%Nucleus Tag (identification number):
Nt=zeros(N1,N2);

%Random numbers:
t1=cputime;
ee=rand(NE,1);
et=rand(NE,1);
t=cputime-t1;
fprintf(1,'timeEE: %8.4f \n',t)

%Process rates:
t1=cputime;
v=zeros(N1,N2,8);
%v(n1,n2,1) = hop rate to neighbor 1
%v(n1,n2,2) = hop rate to neighbor 2
%v(n1,n2,3) = hop rate to neighbor 3
%v(n1,n2,4) = hop rate to neighbor 4
%v(n1,n2,5) = hop rate to neighbor 5
%v(n1,n2,6) = hop rate to neighbor 6
%v(n1,n2,7) = adsorption rate
%v(n1,n2,8) = desorption rate
Rh=0; %Total rate for hops (changes dynamically)
Ra=0; %Total rate for adsorption (changes dynamically)
Rd=0; %Total rate for desorption (changes dynamically)
Ph=0; %Total number of Possible hops (changes dynamically)
Pa=0; %Total number of Possible adsorptions (changes dynamically)
Pd=0; %Total number of Possible desorptions (changes dynamically)
for n1=1:N1
    for n2=1:N2
        [v,Rh,Ra,Rd,Ph,Pa,Pd]=UpdateRatesForCurrentSite_initialize(v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1,n2,N1,N2,So,St);
    end
end
t=cputime-t1;
fprintf(1,'timeVV: %8.4f \n',t)

%Initialize numST:
%numST(1) = Number of sites with ST  = 0 (zero neighbors           = adatom  )
%numST(2) = Number of sites with ST  = 1 (one neighbor             = corner  )
%numST(3) = Number of sites with ST  = 2 (two neighbors            = edge    )
%numST(4) = Number of sites with ST  = 3 (three neighbors          = kink    )
%numST(5) = Number of sites with ST >= 4 (four or more neighbors   = bulk    )
numST=zeros(5,1);
for n1=1:N1
    for n2=1:N2
        if ( So(n1,n2) == 1 )
            numST( St(n1,n2)+1 ) = numST( St(n1,n2)+1 ) + 1;
        end
    end
end
NMA = sum( numST(1:4) ); %Number of Mobile Atoms
NFA = numST(5); %Number of Frozen Atoms
NNI = 0; %Number of nucleations (Number of Nucleated Islands)

%Initialize the vector for counting the numST with time:
%1 : Number of atoms with 0 neighbors
%2 : Number of atoms with 1 neighbor
%3 : Number of atoms with 2 neighbors
%4 : Number of atoms with 3 neighbors
%5 : Number of atoms with 4 neighbors
%6 : Average island radius
%7 : Proportional to the number of islands
%8 : Total hop rate (Rh)
%9 : Total adsorption rate (Ra)
%10: Total desorption rate (Rd)
%11: Total number of possible hops (Ph)
%12: Total number of possible adsopritons (Ph)
%13: Total number of possible desoprtions (Ph)
TnumST=zeros(NE,13);

%Initialize variables for CPU timings:
t_des = 0;
t_ads = 0;
t_hop = 0;
t_dah = 0;

t_RN = 0;
t_LS = 0;
t_BK = 0;
t_PT = 0;
t_Total = 0;

%Initialize simulated time:
t=0;
tt=zeros(NE,1);

%Initialize coverage:
Theta=zeros(NE,1);
ThetaMax=0.3;

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Initialize plots:
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
if 1
figA=figure;
PlotSOnST(So,St,L,numST,N1,N2)%display the boundaries of calculation region 
end

if 1
figB=figure;
ind=1;
%x=ind; %Use this to plot coverage as a function of the number of events
x=tt(ind); %Use this to plot coverage as a function of time
%plot(x,TnumST(ind,1),'.k',x,TnumST(ind,2),'g.',x,TnumST(ind,3),'r.',x,TnumST(ind,4),'b.',x,TnumST(ind,5),'c.')

%figC=figure;
%%plot(x,Theta,'.k')
end

if 1
pause(0.01)
end

%Determine the number of levels for the KLS structure:
Nmax=N1*N2*8;
%[k,g]=determine_k(Nmax);
g = Nmax;   %to perform a simple linear search
%g = 10;     %fixed bin size
%g = 6;
%g = 2500;
%g = 2;     %Binary search
k = ceil( log(Nmax)/log(g) ); %number of levels
fprintf(1,'g = %8i , k = %8i \n',g,k)


%Initialize the k-level search structure:
[rlevel]=initialize_kls_tree(v,k,g);

cell_index_vec=zeros(1,8);
r_old_vec=zeros(1,8);
r_new_vec=zeros(1,8);

pause
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
%Main loop:
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
iend=0;
for ne=1:NE*100
    
    t0=cputime;
    istart=iend+1;
    iend=istart+k-1;
    if iend >= NE
        ee=rand(NE,1);
        iend=0;
        istart=iend+1;
        iend=istart+k-1;
    end
    
    %Search next process:
    t1=cputime;
    R=Rh+Ra+Rd;
    [n1,n2,proc]=linearSearch(v,R,ee(ne),N1,N2);
    %[cell_index,n1,n2,proc]=perform_kls(k,g,rlevel,R,ee(istart:iend),N1,N2);
    
    %increment of time    
    dt = -log( et(ne) )/R;
    t = t + dt;
    tt(ne) = t;
    
    TnumST(ne,8 ) = Rh; %Store current total rate
    TnumST(ne,9 ) = Ra; %Store current total rate
    TnumST(ne,10) = Rd; %Store current total rate
    TnumST(ne,11) = Ph; %Store current total number of possible events
    TnumST(ne,12) = Pa; %Store current total number of possible events
    TnumST(ne,13) = Pd; %Store current total number of possible events
    
    t2=cputime;    
    if proc==8 %Desorption
        
        t3des1=cputime;    

        So(n1,n2) = 0;
        [St,numST,NMA,NFA] = UpdateSTforNeighbors(St,So,n1,n2,N1,N2,-1,numST,NMA,NFA);
       % St(n1,n2) = min(St(n1,n2),4);
        numST( St(n1,n2)+1 ) = numST( St(n1,n2)+1 ) - 1;
        
        [v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1,n2,N1,N2,So,St,g,k);
        [v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForNeighbors(v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1,n2,N1,N2,So,St,rlevel,g,k);  %inside the function, changed by Zhang        

        t3des2=cputime;    
        t_des = t_des + t3des2-t3des1;

    elseif proc==7 %Absorption

        t3ads1=cputime;    

        So(n1,n2) = 1;
        [St,numST,NMA,NFA] = UpdateSTforNeighbors(St,So,n1,n2,N1,N2, 1,numST,NMA,NFA);
        St(n1,n2) = min(St(n1,n2),4);
        numST( St(n1,n2)+1 ) = numST( St(n1,n2)+1 ) + 1;
        
        [v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1,n2,N1,N2,So,St,g,k);
        [v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForNeighbors(v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1,n2,N1,N2,So,St,rlevel,g,k);        

        t3ads2=cputime;    
        t_ads = t_ads + t3ads2-t3ads1;
        
    else %Hop

        t3hop1=cputime;    

        [n1p,n2p]=DetermineFinalSite(n1,n2,N1,N2,proc);
        if ( So(n1,n2) == 1 && So(n1p,n2p) == 0 )           
            %Update occupation and site-type of initial site and its neighborhood:
            So(n1,n2) = 0;
            %St(n1,n2) = min(St(n1,n2),4);
            numST( St(n1,n2)+1 ) = numST( St(n1,n2)+1 ) - 1;
            [St,numST,NMA,NFA] = UpdateSTforNeighbors(St,So,n1,n2,N1,N2,-1,numST,NMA,NFA);

            %Update occupation and site-type of final site and its neighborhood:
            So(n1p,n2p) = 1;
            St(n1p,n2p) = min(St(n1p,n2p),4);
            numST( St(n1p,n2p)+1 ) = numST( St(n1p,n2p)+1 ) + 1;
            [St,numST,NMA,NFA] = UpdateSTforNeighbors(St,So,n1p,n2p,N1,N2, 1,numST,NMA,NFA);            
            %[St,Nt,numST,NMA,NFA,NNI] = UpdateSTforNeighbors(St,So,Nt,n1p,n2p,N1,N2, 1,numST,NMA,NFA,NNI);            
            
            %Update rates of initial site and its neighborhood:
            [v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1,n2,N1,N2,So,St,g,k);       
            [v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForNeighbors(v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1,n2,N1,N2,So,St,rlevel,g,k);
            
            %Update rates of final site and its neighborhood:
            [v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForCurrentSite(rlevel,v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1p,n2p,N1,N2,So,St,g,k);
            [v,Rh,Ra,Rd,Ph,Pa,Pd,rlevel]=UpdateRatesForNeighbors(v,Rh,Ra,Rd,Ph,Pa,Pd,hrt,art,drt,n1p,n2p,N1,N2,So,St,rlevel,g,k);

        end    
        
        t3hop2=cputime;    
        t_hop = t_hop + t3hop2-t3hop1;
       
    end
    
    TnumST(ne,1:5)=numST';
    %TnumST(ne,6)=TnumST(ne,5)/(TnumST(ne,3)+TnumST(ne,4));
    %TnumST(ne,6)=2*(TnumST(ne,3)+TnumST(ne,4)+TnumST(ne,5))/(TnumST(ne,3)+TnumST(ne,4));
    %TnumST(ne,6)=sqrt(TnumST(ne,3)+TnumST(ne,4)+TnumST(ne,5));
    TnumST(ne,6)=max(1,(TnumST(ne,3)+TnumST(ne,4)+TnumST(ne,5))/(TnumST(ne,3)+TnumST(ne,4)+2*sqrt(pi*TnumST(ne,5)))); %if (TnumST(ne,6) == 1), TnumST(ne,6) = 0; end
    %TnumST(ne,7)=(TnumST(ne,3)+TnumST(ne,4)+TnumST(ne,5))*(TnumST(ne,3)+TnumST(ne,4))^2 / TnumST(ne,5)^2;
    %TnumST(ne,7)=(TnumST(ne,3)+TnumST(ne,4))^2/(TnumST(ne,3)+TnumST(ne,4)+TnumST(ne,5)); if (TnumST(ne,7) < 1), TnumST(ne,7) = 0; end
    TnumST(ne,7)=(TnumST(ne,3)+TnumST(ne,4)+TnumST(ne,5))/pi/TnumST(ne,6)^2; if (TnumST(ne,7) < 1), TnumST(ne,7) = 0; end
    Theta(ne) = sum( TnumST(ne,1:5) ) / N1 / N2;

    t3=cputime;  
        
    NT=1000; %Number of events to update plots and output
    if (mod(ne,NT)==0) || Theta(ne) == ThetaMax
        if 1
        figure(figA)
        PlotSOnST(So,St,L,numST,N1,N2)%plotting the shape evolution of atom cluster
        end 
        
        if 1
        figure(figB)
        ind=1:ne;
        %x=ind; %Use this to plot coverage as a function of the number of events
        x=tt(ind); %Use this to plot coverage as a function of time
        bs = 500; %Bin Size
        data = TnumST(ind,8);
        rYh = arrayfun(@(x) mean(data(x:min(x+bs-1,end),:),1),1:bs:size(data,1), 'UniformOutput', false);
        rYh = cat(1, rYh{:});
        data = TnumST(ind,9);
        rYa = arrayfun(@(x) mean(data(x:min(x+bs-1,end),:),1),1:bs:size(data,1), 'UniformOutput', false);
        rYa = cat(1, rYa{:});
        data = TnumST(ind,8)./TnumST(ind,11);
        rYh_n = arrayfun(@(x) mean(data(x:min(x+bs-1,end),:),1),1:bs:size(data,1), 'UniformOutput', false);
        rYh_n = cat(1, rYh_n{:});
        data = TnumST(ind,9)./TnumST(ind,12);
        rYa_n = arrayfun(@(x) mean(data(x:min(x+bs-1,end),:),1),1:bs:size(data,1), 'UniformOutput', false);
        rYa_n = cat(1, rYa_n{:});
        data = TnumST(ind,10);
        rYd = arrayfun(@(x) mean(data(x:min(x+bs-1,end),:),1),1:bs:size(data,1), 'UniformOutput', false);
        rYd = cat(1, rYd{:});
        data = x;
        rX = arrayfun(@(x) mean(data(x:min(x+bs-1,end),:),1),1:bs:size(data,1), 'UniformOutput', false);
        rX = cat(1, rX{:});

        %plot(x,TnumST(ind,1),'.k',x,TnumST(ind,2),'gs',x,TnumST(ind,3),'rd',x,TnumST(ind,4),'bo',x,TnumST(ind,5),'cv')
        %loglog(x,TnumST(ind,1),'.k',x,TnumST(ind,2),'gs',x,TnumST(ind,3),'rd',x,TnumST(ind,4),'bo',x,TnumST(ind,5),'cv')
        %loglog(x,sum(TnumST(ind,11:13),2),'r.',x,sum(TnumST(ind,8:10),2)/sum(TnumST(1,8:10),2)*100,'g+',rX,rY/TnumST(1,8)*100,'y*')
        loglog(x,TnumST(ind,11),'rs',x,TnumST(ind,12),'go') %,x,TnumST(ind,13),'bd')
        hold on
        %loglog(x,TnumST(ind,8)/TnumST(1,8)*100,'r+',rX,rYh/TnumST(1,8)*100,'k*')
        loglog(x,TnumST(ind,8),'m+',rX,rYh,'ko')
        loglog(x,TnumST(ind,9),'rx',rX,rYa,'ko')
        loglog(x,TnumST(ind,8)./TnumST(ind,11),'cv',rX,rYh_n,'ks')
        loglog(x,TnumST(ind,9)./TnumST(ind,12),'b^',rX,rYa_n,'ks')
        %loglog(x,TnumST(ind,10),'b*',rX,rYd,'ko')        
        loglog(x,Theta(ind)*1e4,'kd',x,TnumST(ind,2),'gs',x,TnumST(ind,1),'k.',x,TnumST(ind,3),'rd',x,TnumST(ind,4),'bo',x,TnumST(ind,5),'cv')
        loglog(x,TnumST(ind,6),'y^',x,TnumST(ind,7),'mx')
        
        %figure(figC)
        %%plot(x,Theta(ind),'.k')
        %loglog(x,Theta(ind),'.k')
        end
        
        if 0
        fprintf(1,'ne: %10i / GenerateRandoms: %6.4f / SearchNextEvent: %6.4f / Bookkeeping: %6.4f / Plotting: %6.4f / Coverage: %6.4f / Total CPU time: %8.4f\n',ne,t_RN/t_Total,t_LS/t_Total,t_BK/t_Total,t_PT/t_Total,Theta(ne),t_Total)
        fprintf(1,' t: %6.4e / Des: %6.4f / Ads: %6.4f / Hop: %6.4f \n',t,t_des/t_dah,t_ads/t_dah,t_hop/t_dah)
        end
                
        if 1
        %pause(0.01)
        end

    end      
    
    t4=cputime;
    
    t_dah = t_des + t_ads + t_hop;
    
    t_RN = t_RN + t1-t0;
    t_LS = t_LS + t2-t1;
    t_BK = t_BK + t3-t2;
    t_PT = t_PT + t4-t3;
    t_Total = t_Total + t4-t1;
    
    if Theta(ne) == ThetaMax
        break
    end
end
fprintf(1,'LinearSearch: %6.4f / Bookkeeping: %6.4f / Plotting: %6.4f / Totaltime: %6.4f \n',t_LS,t_BK,t_PT,t_Total)
%filename=['run' num2str(run,'%2.2i') '.mat'];
%save(filename,'ne','tt','Theta','TnumST');

return














%%%%%%%%%%%%%%%%%%
%N1=N2=100;
% KLS with g = 2: 
%ne:      30500 / GenerateRandoms: 0.0005 / SearchNextEvent: 0.0282 / Bookkeeping: 0.9621 / Plotting: 0.0096 / Coverage: 0.0206 / Total CPU time: 280.5000
% t: 4.0189e-09 / Des: 0.0000 / Ads: 0.0030 / Hop: 0.9971 

% KLS with g = Nmax = N1*N2*8: 
%ne:      36500 / GenerateRandoms: 0.0002 / SearchNextEvent: 0.2226 / Bookkeeping: 0.7516 / Plotting: 0.0257 / Coverage: 0.0203 / Total CPU time: 134.7500
% t: 4.0890e-09 / Des: 0.0000 / Ads: 0.0033 / Hop: 0.9967 

% KLS with g = 6: 
%ne:      31500 / GenerateRandoms: 0.0005 / SearchNextEvent: 0.0203 / Bookkeeping: 0.9680 / Plotting: 0.0117 / Coverage: 0.0205 / Total CPU time: 334.1719
% t: 4.3950e-09 / Des: 0.0000 / Ads: 0.0039 / Hop: 0.996
 
% LS:
%ne:      33000 / GenerateRandoms: 0.0004 / SearchNextEvent: 0.4294 / Bookkeeping: 0.5435 / Plotting: 0.0271 / Coverage: 0.0204 / Total CPU time: 112.5781
% t: 4.1848e-09 / Des: 0.0000 / Ads: 0.0026 / Hop: 0.9974
%ne:      31500 / GenerateRandoms: 0.0012 / SearchNextEvent: 0.4193 / Bookkeeping: 0.5562 / Plotting: 0.0245 / Coverage: 0.0206 / Total CPU time: 101.2813
% t: 4.8540e-09 / Des: 0.0000 / Ads: 0.0031 / Hop: 0.997
 

%%%%%%%%%%%%%%%%%%
%N1=N2=150;
% KLS with g = 2: 
%ne:       1500 / GenerateRandoms: 0.0000 / SearchNextEvent: 0.0172 / Bookkeeping: 0.9782 / Plotting: 0.0046 / Coverage: 0.0011 / Total CPU time:  27.2656
% t: 2.4056e-10 / Des: 0.0000 / Ads: 0.0077 / Hop: 0.9935
%ne:      37000 / GenerateRandoms: 0.0002 / SearchNextEvent: 0.0189 / Bookkeeping: 0.9749 / Plotting: 0.0062 / Coverage: 0.0104 / Total CPU time: 724.0781
% t: 2.1748e-09 / Des: 0.0000 / Ads: 0.0032 / Hop: 0.9968 
%ne:      70500 / GenerateRandoms: 0.0002 / SearchNextEvent: 0.0183 / Bookkeeping: 0.9759 / Plotting: 0.0057 / Coverage: 0.0206 / Total CPU time: 1342.0000
% t: 4.5514e-09 / Des: 0.0000 / Ads: 0.0033 / Hop: 0.9968
%ne:     109000 / GenerateRandoms: 0.0002 / SearchNextEvent: 0.0181 / Bookkeeping: 0.9763 / Plotting: 0.0056 / Coverage: 0.0402 / Total CPU time: 2051.0156
% t: 8.6026e-09 / Des: 0.0000 / Ads: 0.0040 / Hop: 0.9960

% LS:
%ne:       2500 / GenerateRandoms: 0.0000 / SearchNextEvent: 0.3427 / Bookkeeping: 0.6395 / Plotting: 0.0177 / Coverage: 0.0012 / Total CPU time:  23.7969
% t: 3.0838e-10 / Des: 0.0000 / Ads: 0.0083 / Hop: 0.9927
%ne:      40500 / GenerateRandoms: 0.0004 / SearchNextEvent: 0.3830 / Bookkeeping: 0.6043 / Plotting: 0.0127 / Coverage: 0.0102 / Total CPU time: 388.1875
% t: 2.1229e-09 / Des: 0.0000 / Ads: 0.0034 / Hop: 0.9966
 
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%g=2;
%ne:    19500 / GenerateRandoms: 0.0000 / SearchNextEvent: 0.0497 / Bookkeeping: 0.9263 / Plotting: 0.0239 / Coverage: 0.1032 / Total CPU time:  49.6406
%g=10;
%ne:    21000 / GenerateRandoms: 0.0003 / SearchNextEvent: 0.0186 / Bookkeeping: 0.9680 / Plotting: 0.0135 / Coverage: 0.1012 / Total CPU time:  91.7500
%g=k approx
%ne:    23000 / GenerateRandoms: 0.0006 / SearchNextEvent: 0.0324 / Bookkeeping: 0.9383 / Plotting: 0.0293 / Coverage: 0.1040 / Total CPU time:  49.1250
%g=Nmax;
%ne:    19000 / GenerateRandoms: 0.0004 / SearchNextEvent: 0.0830 / Bookkeeping: 0.8834 / Plotting: 0.0336 / Coverage: 0.1016 / Total CPU time:  35.7813
%ne:    21500 / GenerateRandoms: 0.0017 / SearchNextEvent: 0.1358 / Bookkeeping: 0.8193 / Plotting: 0.0449 / Coverage: 0.1048 / Total CPU time:  27.1563
%ne:    30500 / GenerateRandoms: 0.0004 / SearchNextEvent: 0.0978 / Bookkeeping: 0.8566 / Plotting: 0.0456 / Coverage: 0.2044 / Total CPU time:  41.8438

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%plotting the average iusses 
% numRun=11;
% LoadAndPlot(numRun)

%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

%counting the number of various szie types
%x=1:NE;
%plot(x,TnumST(x,1),'-k',x,TnumST(x,2),'g-')
  
%ne,numST(2),'g-',ne,numST(3),'r-',ne,numST(4),'b-')

%,x,TnumST(x,2),'g-'...     
   %  ,x,TnumST(x,3),'r-',x,TnumST(x,4),'b-'
