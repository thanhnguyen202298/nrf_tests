package no.nordicsemi.android.nrfmesh.viewmodels;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import no.nordicsemi.android.mesh.Provisioner;
import no.nordicsemi.android.nrfmesh.keys.AppKeysActivity;

/**
 * ViewModel for {@link AppKeysActivity}
 */
public class EditProvisionerViewModel extends BaseViewModel {

    @Inject
    EditProvisionerViewModel(@NonNull final NrfMeshRepository nrfMeshRepository) {
        super(nrfMeshRepository);
    }

    public void setSelectedProvisioner(@NonNull final Provisioner provisioner) {
        mNrfMeshRepository.setSelectedProvisioner(provisioner);
    }

    public LiveData<Provisioner> getSelectedProvisioner() {
        return mNrfMeshRepository.getSelectedProvisioner();
    }
}
