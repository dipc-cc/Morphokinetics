load('tmpHist_010.mat')
ha_10=[Hist_angles(180) (Hist_angles(181:359)+Hist_angles(179:-1:1))/2 Hist_angles(360)];
he_10=Hist_events;
hh_10=[Hist_hops(180) (Hist_hops(181:359)+Hist_hops(179:-1:1))/2 Hist_hops(360)];

load('tmpHist_020.mat')
ha_20=[Hist_angles(180) (Hist_angles(181:359)+Hist_angles(179:-1:1))/2 Hist_angles(360)];
he_20=Hist_events;
hh_20=[Hist_hops(180) (Hist_hops(181:359)+Hist_hops(179:-1:1))/2 Hist_hops(360)];

load('tmpHist_040.mat')
ha_40=[Hist_angles(180) (Hist_angles(181:359)+Hist_angles(179:-1:1))/2 Hist_angles(360)];
he_40=Hist_events;
hh_40=[Hist_hops(180) (Hist_hops(181:359)+Hist_hops(179:-1:1))/2 Hist_hops(360)];

load('tmpHist_080.mat')
ha_80=[Hist_angles(180) (Hist_angles(181:359)+Hist_angles(179:-1:1))/2 Hist_angles(360)];
he_80=Hist_events;
hh_80=[Hist_hops(180) (Hist_hops(181:359)+Hist_hops(179:-1:1))/2 Hist_hops(360)];

load('tmpHist_160.mat')
ha_160=[Hist_angles(180) (Hist_angles(181:359)+Hist_angles(179:-1:1))/2 Hist_angles(360)];
he_160=Hist_events;
hh_160=[Hist_hops(180) (Hist_hops(181:359)+Hist_hops(179:-1:1))/2 Hist_hops(360)];

load('tmpHist_320.mat')
ha_320=[Hist_angles(180) (Hist_angles(181:359)+Hist_angles(179:-1:1))/2 Hist_angles(360)];
he_320=Hist_events;
hh_320=[Hist_hops(180) (Hist_hops(181:359)+Hist_hops(179:-1:1))/2 Hist_hops(360)];

x0=[0.3 1:180]; 
y0_10=ha_10;
y0_20=ha_20;
y0_40=ha_40;
y0_80=ha_80;
y0_160=ha_160;
y0_320=ha_320;

[XX,YY]=meshgrid(1:181,[10 20 40 80 160 320]);
ZZ(1,:)=y0_10;
ZZ(2,:)=y0_20;
ZZ(3,:)=y0_40;
ZZ(4,:)=y0_80;
ZZ(5,:)=y0_160;
ZZ(6,:)=y0_320;

%%% Remove the zeros and replace with the lowest number found.
% ZZtmp = ZZ;
ZZ(~ZZ) = 0.5;
% minPerRow = min(ZZ,[],2);
% ZZ(ZZtmp == 0) = min(minPerRow);

%%% Show the graph (only for fun)
figure
surf(XX,YY,log10(ZZ))
shading('interp')
lighting('flat')
hold on

%%% Interpolate
[XXI,YYI]=meshgrid(1:181,[10:320]);
ZZI=10.^(interp2(XX,YY,log10(ZZ),XXI,YYI,'linear'));
surf(XXI,YYI,log10(ZZI))
shading('interp')
lighting('flat')

%%% Finally, save the output file
% Text
fid=fopen('reentranceAtomsFromStatisticsAndInterpolation.txt','w');
fprintf(fid,'# 182 311\n');
for i=1:size(ZZI,1)
  for j=1:size(ZZI,2)
    fprintf(fid,'%12.8e ',ZZI(i,j));
  end
  fprintf(fid,'%12.8e\n',sum(ZZI(i,:)));
end
fclose(fid);
% Binary
save('reentranceAtomsStatisticsAndInterpolation.mat','ZZI')
