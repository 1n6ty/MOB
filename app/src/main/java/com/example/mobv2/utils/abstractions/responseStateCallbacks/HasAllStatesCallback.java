package com.example.mobv2.utils.abstractions.responseStateCallbacks;

public interface HasAllStatesCallback<C1, C2, C3> extends HasOkStateCallback<C1>, HasBadStateCallback<C2>, HasFailStateCallback<C3>
{
}
