%% Open raw data
open('zzi_interpolated_raw_data.mat');
epsilon=1e-5;
zz=ans.ZZI;
for i=1:size(zz,1)
    sumI=sum(zz(i,:));
    iter=0;
    while (sumI + epsilon < 1.0 ) 
      iter=iter+1;
      rest=1-sumI;
      fprintf(1,'raw %d is different. rest=%8.4f\n', i,rest);
      zz(i,:)=zz(i,:)+zz(i,:)*rest;
      sumI=sum(zz(i,:));
      fprintf(1,'the new sum is %8.4f. iter=%d\n', sumI,iter);
    end
end

%% Save processed data
save('zz_interpolated_data.mat','zz');

%% Save processed data in text mode
fid=fopen('zz_interpolated_data.txt','w'); 
for i=1:size(zz,1)
  for j=1:size(zz,2)
    fprintf(fid,'%12.8e ',zz(i,j));
  end
  fprintf(fid,'\n'); 
end
fclose(fid);
