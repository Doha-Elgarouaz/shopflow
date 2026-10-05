import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import api from '@/lib/api';
import { ApiPage, CreateOrderRequest, Order } from '@/lib/types';

export const useMyOrders = (page = 0, size = 10) =>
  useQuery<ApiPage<Order>>({
    queryKey: ['orders', 'my', page],
    queryFn: () => api.get(`/api/orders/my?page=${page}&size=${size}`).then((r) => r.data),
  });

export const useOrder = (id: number) =>
  useQuery<Order>({
    queryKey: ['order', id],
    queryFn: () => api.get(`/api/orders/${id}`).then((r) => r.data),
    enabled: !!id,
  });

export const useCreateOrder = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (request: CreateOrderRequest) =>
      api.post('/api/orders', request).then((r) => r.data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['orders'] }),
  });
};

export const useCancelOrder = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (id: number) => api.patch(`/api/orders/${id}/cancel`).then((r) => r.data),
    onSuccess: () => queryClient.invalidateQueries({ queryKey: ['orders'] }),
  });
};
