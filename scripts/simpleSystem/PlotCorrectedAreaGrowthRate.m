%Load data from files:
A=load('Total rate.txt');
B=load('Area growth rate.txt');
t1=A(:,1); %Inverse temperature (1/(kB*T)) for data 1
R=A(:,2); %Total rate
t2=B(:,1); %Inverse temperature (1/(kB*T)) for data 2
S=B(:,2); %Area grwoth rate

%t1 and t2 have different lengths. Resample them to construct arrays with the same length: 
t1p=resample(t1,100,length(t1));
Rp=resample(R,100,length(R));
t2p=resample(t2,100,length(t2));
Sp=resample(S,100,length(S));
%Remove four points at left and right, to avoid interpolation artifacts at both ends:
t1p=t1p(4:end-4);
t2p=t2p(4:end-4);
Rp=Rp(4:end-4);
Sp=Sp(4:end-4);

%Arrhenius plot of the resampled data (choose 1 or 2):
%figure, semilogy(t1p,Rp,'bo',t1p,Sp,'rs') %1
%figure, semilogy(t2p,Rp,'bo',t2p,Sp,'rs') %2

%fractal dimension:
%d=2 for compact islands
%d=1.83 for dendritic islands
%d=linear from 2 to 1.83 in between
i4=length(t1p); i1=34; i2=33; i3=i4-i1-i2; 
%i4=length(t1p); i1=33; i2=32; i3=i4-i1-i2; 
d=[2*ones(i1,1); 2-(2-1.83)/(i2)*transpose([1:i2]); 1.83*ones(i3,1)];
figure, plot(t1p,d,'x-')
set(gca,'Xlim',[50 100])
set(gca,'Ylim',[1.75 2.25])
xlabel('Inverse temperature, 1/(kB*T)')
legend('fractal dimension, d')

%Arrhenius plot of total rate, area growth rate and corrected area growth rate:
figure, semilogy(t1p,Sp,'rs',t1p,0.5*Rp,'bo',t1p,Sp.^(d/2),'c.')
set(gca,'Xlim',[50 100])
xlabel('Inverse temperature, 1/(kB*T)')
legend('Area growth rate, c','Total rate, const*R','Corrected area growth rate, c^d')



