function [rlevel] = initialize_kls_tree(v,k,g)
%
%Initialize the K-Level Search "tree" structure
%

Nmax=numel(v);

rlevel = struct('element',cellstr('[]'));
for lev = k:-1:1
  rlevel(lev).element = zeros(1,g^lev);
  if ( lev == k )
      %t1=cputime;
      %rlevel(lev).element(1:Nmax) = reshape(v,1,Nmax);
      i=0;
      for n1=1:size(v,1)
          for n2=1:size(v,2)
              for proc=1:8
                  i=i+1;
                  rlevel(lev).element(i) = v(n1,n2,proc);
              end
          end
      end
     % t=cputime-t1;
     % fprintf(1,'time1: %8.4f \n',t)
  else
   % t2=cputime;
    for i = 1:g^lev
      istart = (i-1)*g+1;
      iend = i*g;
      rlevel(lev).element(i) = sum(rlevel(lev+1).element(istart:iend));
    end  
  %  t=cputime-t2;
   % fprintf(1,'time2: %8.4f \n',t)
  end
end

return
